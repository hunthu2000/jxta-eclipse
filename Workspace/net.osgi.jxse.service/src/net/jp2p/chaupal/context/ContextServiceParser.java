package net.jp2p.chaupal.context;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;

import net.jp2p.container.ContainerFactory;
import net.jp2p.container.builder.IFactoryBuilder;
import net.jp2p.container.context.ContextLoader;
import net.jp2p.container.utils.IOUtils;
import net.jp2p.container.utils.Utils;

public class ContextServiceParser {

	protected static final String JAXP_SCHEMA_SOURCE =
		    "http://java.sun.com/xml/jaxp/properties/schemaSource";
	protected static final String JP2P_XSD_SCHEMA = "http://www.condast.com/saight/jp2p-schema.xsd";

	private static final String S_ERR_NO_SCHEMA_FOUND = "The XML Schema was not found";
	private static final String S_WRN_NOT_NAMESPACE_AWARE = "The parser is not validating or is not namespace aware";
	
	static final String S_DOCUMENT_ROOT = "DOCUMENT_ROOT";

	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";	
		
	private boolean completed, failed;
	private URL url;
	private Collection<ServiceInfo> services;
	
	private Logger logger = Logger.getLogger( ContextServiceParser.class.getName() );
	
	public ContextServiceParser( Class<?> clss, ContextLoader contexts ) {
		this( clss.getResource( IFactoryBuilder.S_DEFAULT_LOCATION ), clss, contexts );
		services = new ArrayList<ServiceInfo>();
	}

	/**
	 * Build the factories from the given resource in the class file and add them to the container
	 * @param bundleId
	 * @param clss
	 * @param location
	 * @param builder
	 */
	public ContextServiceParser( URL url, Class<?> clss, ContextLoader contexts ) {
		this.url = url;
		this.completed = false;
		this.failed = false;
	}

	/**
	 * Returns true if the url points to a valid resource
	 * @return
	 */
	public boolean canCreate(){
		if( url == null )
			return false;
		try {
			return ( url.openConnection().getContentLengthLong() > 0 );
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	
	/* (non-Javadoc)
	 * @see net.osgi.jp2p.chaupal.xml.IFactoryBuilder#build()
	 */
	public void parse() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		URL schema_in = ContextServiceParser.class.getResource( IFactoryBuilder.S_SCHEMA_LOCATION); 
		if( schema_in == null )
			throw new RuntimeException( S_ERR_NO_SCHEMA_FOUND );
		
		//factory.setNamespaceAware( true );
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// note that if your XML already declares the XSD to which it has to conform, then there's no need to create a validator from a Schema object
		Source schemaFile = new StreamSource( ServiceHandler.class.getResourceAsStream( IFactoryBuilder.S_SCHEMA_LOCATION ));
		InputStream in;
		try {
			in = url.openStream();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		//First parse the XML file
		ContainerFactory root = null;
		try {
			//Schema schema = schemaFactory.newSchema(schemaFile);
			//factory.setSchema(schema);//saxParser.
			
			SAXParser saxParser = factory.newSAXParser();
			if( !saxParser.isNamespaceAware() )
				logger.warning( S_WRN_NOT_NAMESPACE_AWARE );
			
			//saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA); 
			//saxParser.setProperty(JAXP_SCHEMA_SOURCE, new File(JP2P_XSD_SCHEMA)); 
			ServiceHandler handler = new ServiceHandler( services );
			saxParser.parse( in, handler);
		} catch( SAXNotRecognizedException e ){
			failed = true;
			e.printStackTrace();			
		} catch (ParserConfigurationException e) {
			failed = true;
			e.printStackTrace();
		} catch (SAXException e) {
			failed = true;
			e.printStackTrace();
		} catch (IOException e) {
			failed = true;
			e.printStackTrace();
		}
		finally{
			IOUtils.closeInputStream(in);
		}
		
		this.completed = true;
	}

	public boolean complete() {
		this.completed = true;
		return completed;
	}

	/* (non-Javadoc)
	 * @see net.osgi.jp2p.chaupal.xml.IFactoryBuilder#isCompleted()
	 */
	public boolean isCompleted() {
		return completed;
	}

	public boolean hasFailed() {
		return failed;
	}

	public static String getLocation( String defaultLocation ){
		if( !Utils.isNull( defaultLocation ))
			return defaultLocation;
		return defaultLocation;
	}
}