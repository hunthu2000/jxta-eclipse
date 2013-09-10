package net.osgi.jxse.service.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.CompositeBuilder;
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.ICompositeBuilder;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.builder.ICompositeBuilderListener.FactoryEvents;
import net.osgi.jxse.context.IJxseServiceContext.ContextDirectives;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.factory.ComponentFactoryEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.network.NetworkConfigurationFactory;
import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jxse.network.NetworkManagerFactory;
import net.osgi.jxse.network.NetworkManagerPropertySource;
import net.osgi.jxse.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jxse.preferences.properties.IJxsePropertySource;
import net.osgi.jxse.seeds.SeedListFactory;
import net.osgi.jxse.service.xml.PreferenceStore.Persistence;
import net.osgi.jxse.service.xml.PreferenceStore.SupportedAttributes;
import net.osgi.jxse.service.xml.XMLComponentFactory.Groups;
import net.osgi.jxse.utils.ProjectFolderUtils;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;
import net.osgi.jxse.utils.io.IOUtils;

public class XMLComponentFactory implements IComponentFactory<NetworkManager, ContextProperties, ContextDirectives>, ICompositeBuilder<NetworkManager>, ICompositeBuilderListener {

	protected static final String JAXP_SCHEMA_SOURCE =
		    "http://java.sun.com/xml/jaxp/properties/schemaSource";
	protected static final String JXSE_XSD_SCHEMA = "http://www.condast.com/saight/jxse-schema.xsd";

	private static final String S_ERR_NO_SCHEMA_FOUND = "The XML Schema was not found";
	private static final String S_WRN_NOT_NAMESPACE_AWARE = "The parser is not validating or is not namespace aware";
	
	static final String S_DOCUMENT_ROOT = "DOCUMENT_ROOT";

	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";	
	
	public static String S_DEFAULT_FOLDER = "/JXSE-INF";
	public static String S_DEFAULT_LOCATION = S_DEFAULT_FOLDER + "/jxse-1.0.0.xml";
	public static String S_SCHEMA_LOCATION =  S_DEFAULT_FOLDER + "/jxse-schema.xsd";
	
	public enum Groups{
		PROPERTIES,
		DIRECTIVES,
		SEEDS;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public static boolean isGroup( String str ){
			str = StringStyler.styleToEnum(str);
			if(( str == null ) || ( str.length() == 0 ))
				return false;
			for( Groups comp: values()){
				if( comp.name().equals( str.toUpperCase() ))
					return true;
			}
			return false;
		}
	}
	
	private Class<?> clss;
	private boolean completed, failed;
	private JxseContextPropertySource properties;
	private String location;
	
	private Collection<ICompositeBuilderListener> listeners;
	
	//private Logger logger = Logger.getLogger(XMLComponentFactory.class.getName() );
	
	private NetworkManager module;
	
	private Logger logger = Logger.getLogger( XMLComponentFactory.class.getName() );
	
	public XMLComponentFactory( String pluginId, Class<?> clss) {
		this( pluginId, clss, S_DEFAULT_LOCATION );
	}

	protected XMLComponentFactory( String pluginId, Class<?> clss, String location ) {
		this.clss = clss;
		this.location = location;
		this.completed = false;
		this.failed = false;
		properties = new JxseContextPropertySource( pluginId, location);
		this.listeners = new ArrayList<ICompositeBuilderListener>();
	}

	/**
	 * Get the properties
	 * @return
	 */
	JxseContextPropertySource getProperties() {
		return properties;
	}

	/**
	 * If true, the context can be started automatically
	 * @return
	 */
	public boolean isAutostart(){
		return Boolean.parseBoolean( (String) properties.getDirective( ContextDirectives.AUTO_START ));
	}
	
	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#addListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	@Override
	public void addListener( ICompositeBuilderListener listener ){
		this.listeners.add( listener);
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#removeListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	@Override
	public void removeListener( ICompositeBuilderListener listener ){
		this.listeners.remove( listener);
	}

	void notifyListeners( ComponentFactoryEvent event ){
		for( ICompositeBuilderListener listener: listeners )
			listener.notifyFactoryCreated(event);
	}

	@Override
	public boolean canCreate(){
		if( clss == null )
			return false;
		return ( clss.getResourceAsStream( location ) != null );
	}
	
	@Override
	public NetworkManager createModule() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		URL schema_in = XMLComponentFactory.class.getResource( S_SCHEMA_LOCATION); 
		if( schema_in == null )
			throw new RuntimeException( S_ERR_NO_SCHEMA_FOUND );
		
		//factory.setNamespaceAware( true );
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// note that if your XML already declares the XSD to which it has to conform, then there's no need to create a validator from a Schema object
		Source schemaFile = new StreamSource( JxtaHandler.class.getResourceAsStream( S_SCHEMA_LOCATION ));
		InputStream in = clss.getResourceAsStream( location );
		try {
			logger.info("Parsing JXSE Bundle: " + this.properties.getProperty( ContextProperties.PLUGIN_ID ));
			//Schema schema = schemaFactory.newSchema(schemaFile);
			//factory.setSchema(schema);//saxParser.
			
			SAXParser saxParser = factory.newSAXParser();
			if( !saxParser.isNamespaceAware() )
				logger.warning( S_WRN_NOT_NAMESPACE_AWARE );
			
			//saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA); 
			//saxParser.setProperty(JAXP_SCHEMA_SOURCE, new File(JXSE_XSD_SCHEMA)); 
			JxtaHandler handler = new JxtaHandler( this );
			saxParser.parse( in, handler);
			this.completed = true;
			if( handler.getNode() == null )
				return null;
			CompositeBuilder<NetworkManager, ContextProperties, ContextDirectives> cf = 
					new CompositeBuilder<NetworkManager, ContextProperties, ContextDirectives>( handler.getNode() );
			cf.addListener( this );
			module = cf.createModule();
			cf.removeListener(this);
			logger.info("JXSE Bundle Parsed: " + this.properties.getProperty( ContextProperties.PLUGIN_ID ));
			return module;
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
		return module;
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public boolean hasFailed() {
		return failed;
	}

	@Override
	public NetworkManager getModule() {
		return module;
	}

	@Override
	public Components getComponentName() {
		return Components.NETWORK_MANAGER;
	}

	@Override
	public void notifyFactoryCreated(ComponentFactoryEvent event) {
		this.notifyListeners(event);
	}

	@Override
	public IJxsePropertySource<ContextProperties, ContextDirectives> getPropertySource() {
		return this.properties;
	}
	
	public static String getLocation( String defaultLocation ){
		if( !Utils.isNull( defaultLocation ))
			return defaultLocation;
		return defaultLocation;
	}
}

class JxtaHandler extends DefaultHandler{

	private XMLComponentFactory owner;
	private ComponentNode<NetworkManager, ContextProperties, ContextDirectives> root;
	private ComponentNode<?,?,?> currentNode;
	private Components current;
	private Groups group;
	private String groupValue;
	private boolean newProperty = false;
	private SeedListFactory sdf;
	private Attributes attributes;
	private PreferenceStore store;

	private static Logger logger = Logger.getLogger( XMLComponentFactory.class.getName() );
	
	public JxtaHandler( XMLComponentFactory owner ) {
		super();
		this.owner = owner;
	}

	ComponentNode<NetworkManager, ContextProperties, ContextDirectives> getNode() {
		return root;
	}

	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void startElement(String uri, String localName,String qName, 
			Attributes attributes) throws SAXException {

		IComponentFactory<?,?,?> factory = null;
		this.attributes = attributes;
		if( Components.isComponent( qName )){
			current = Components.valueOf( StringStyler.styleToEnum( qName ));

			switch( current ){
			case JXSE_CONTEXT:
				store = new PreferenceStore(( String )owner.getPropertySource().getProperty( ContextProperties.PLUGIN_ID ));
				break;
			case NETWORK_MANAGER:
				NetworkManagerPropertySource source = new NetworkManagerPropertySource( (JxseContextPropertySource) owner.getPropertySource() );
				factory = new NetworkManagerFactory( source );
				this.root = new ComponentNode( factory );
				break;
			case NETWORK_CONFIGURATOR:
				factory = new NetworkConfigurationFactory(( NetworkManagerFactory )this.currentNode.getFactory() );
				break;
			default:
				break;
			}
			if( factory == null )
				return;
			if( this.currentNode == null ){
				this.currentNode = root;
			}
			else
				this.currentNode = this.currentNode.addChild(factory);
		}else if( Groups.isGroup( qName )){
			group = Groups.valueOf( StringStyler.styleToEnum( qName ));
			this.groupValue = null;
			if( group.equals( Groups.SEEDS ))
				this.sdf = new SeedListFactory();
		}else{
			String str =  StringStyler.styleToEnum( qName );
			if(( this.groupValue == null ) ||( this.groupValue.equals(XMLComponentFactory.S_DOCUMENT_ROOT )))
				this.groupValue = str;
			else
				this.groupValue += "." + str;
			this.newProperty = true;
		}
		if( this.groupValue != null )
			logger.info(" Group value: " + this.groupValue );
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if( Components.isComponent( qName )){
			current = Components.valueOf( StringStyler.styleToEnum( qName ));
			switch( current ){
			case NETWORK_CONFIGURATOR:
				NetworkConfigurationFactory ncf = (NetworkConfigurationFactory) this.currentNode.getFactory();
				if(( sdf != null ) && !sdf.isEmpty() )
					ncf.addSeedlist(this.sdf);
				break;
			case JXSE_CONTEXT:
				this.group = null;
			default:
				break;
			}
			logger.info("COMPLETING COMPONENT :" + qName);
			if( this.currentNode == null )
				return;
			if( this.currentNode.getFactory() != null )
				owner.notifyListeners( new ComponentFactoryEvent( owner, this.currentNode.getFactory(), FactoryEvents.FACTORY_CREATED ));
			this.currentNode = this.currentNode.getParent();
		}else if( Groups.isGroup( qName )){
			if( Groups.PROPERTIES.equals( group ) || Groups.DIRECTIVES.equals( group ))
				this.groupValue = null;
			group = null;
			
		}else if( this.groupValue != null ){
			if( this.newProperty ){
				if(( this.attributes != null ) && ( this.attributes.getLength() > 0 )){
					Map<SupportedAttributes, String> attrs =  JxseXMLPreferences.parseAttributes(attributes); 
					//this.preferences.putProperty( attrs, StringStyler.prettyString( this.groupValue ), null);
					
				}
				this.newProperty = false;
			}
			logger.info("Ending group value: " + this.groupValue );
			String[] split = this.groupValue.split("[.]");
			if( this.groupValue.equals( split[0] )){
				this.groupValue = null; 
			}else
				this.groupValue = this.groupValue.replace("." + split[ split.length - 1], "");
			}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		this.parseDirectives(ch, start, length);
		this.parseProperties(ch, start, length);
		this.parseSeeds(ch, start, length);
		this.newProperty = false;
	}
	
	/**
	 * Parse the directives
	 * @param ch
	 * @param start
	 * @param length
	 * @throws SAXException
	 */
	protected void parseSeeds(char ch[], int start, int length) throws SAXException {
		if( !Groups.SEEDS.equals(group ) || ( this.groupValue == null ))
			return;

		String value = new String(ch, start, length);
		if(( value == null ) || ( value.trim().length() == 0))
			return;
		
		Components component = Components.JXSE_CONTEXT;
		if(( this.currentNode != null ) && ( this.currentNode.getFactory() != null )){
			component = this.currentNode.getFactory().getComponentName();
		}
		switch( component ){
		case NETWORK_CONFIGURATOR:
			this.sdf.addSeed(this.groupValue, value);
			break;
		default:
			break;
		}
	}

	/**
	 * Parse the directives
	 * @param ch
	 * @param start
	 * @param length
	 * @throws SAXException
	 */
	protected void parseDirectives(char ch[], int start, int length) throws SAXException {
		if( !Groups.DIRECTIVES.equals(group ) || ( this.groupValue == null ))
			return;

		String value = new String(ch, start, length);
		if(( value == null ) || ( value.trim().length() == 0))
			return;
		
 		ContextDirectives directive = ContextDirectives.valueOf(this.groupValue);
		
		Components component = Components.JXSE_CONTEXT;
		if(( this.currentNode != null ) && ( this.currentNode.getFactory() != null )){
			component = this.currentNode.getFactory().getComponentName();
		}
		switch( component ){
		case JXSE_CONTEXT:
			this.owner.getPropertySource().setDirective( directive, value);
			break;
		default:
			//this.currentNode.getFactory().getPropertySource().setDirective( directive, value );
		}
	}

	/**
	 * Parse the properties
	 * @param ch
	 * @param start
	 * @param length
	 * @throws SAXException
	 */
	protected void parseProperties(char ch[], int start, int length) throws SAXException {
		if( !Groups.PROPERTIES.equals(group ) || ( this.groupValue == null ) || ( !this.newProperty ))
			return;
		
		String value = new String(ch, start, length);
		if( Utils.isNull( value  ))
			return;

		Map<SupportedAttributes, String> attrs =  JxseXMLPreferences.parseAttributes(attributes); 
		//owner.getPropertySource().putProperty( attrs, StringStyler.prettyString( this.groupValue ), value);
		
		Components component = Components.JXSE_CONTEXT;
		if(( this.currentNode != null ) && ( this.currentNode.getFactory() != null )){
			component = this.currentNode.getFactory().getComponentName();
		}
		
		String key = this.groupValue.replace(".", "_8" );
		switch( component ){
		case JXSE_CONTEXT:
			JxseContextPropertySource source = (JxseContextPropertySource) this.owner.getPropertySource();
			if( this.groupValue.toUpperCase().equals( ContextProperties.IDENTIFIER.name() ))
				source.setIdentifier( value );
			else
				source.setProperty( ContextProperties.valueOf(this.groupValue.toUpperCase()), value);
			break;
		case NETWORK_MANAGER:
			NetworkManagerFactory nmf = (NetworkManagerFactory) this.currentNode.getFactory();
			NetworkManagerProperties nmp = NetworkManagerProperties.valueOf( key );
			switch( nmp ){
			case INSTANCE_HOME:
				File file = new File( ProjectFolderUtils.getParsedUserDir( value, (String) this.owner.getPropertySource().getProperty( ContextProperties.PLUGIN_ID ) ));
				//nmf.setInstanceHomeFolder( file.getAbsolutePath() );
				break;
			default:
				nmf.addProperty(nmp, StringStyler.styleToEnum( value ));
				break;
			}
			break;
		case NETWORK_CONFIGURATOR:
			NetworkConfigurationFactory ncf = ( NetworkConfigurationFactory )this.currentNode.getFactory();
			NetworkConfiguratorProperties ncp = NetworkConfiguratorProperties.valueOf( key );
			ncf.addProperty(ncp, value);
			break;
		default:
			break;
		}
	}
	
	protected PreferenceStore getPreferences( String pluginId, Map<SupportedAttributes, String> attrs ){
		if(( attrs == null ) || ( attrs.size() == 0 ))
			return null;
		Persistence ps = Persistence.valueOf( attrs.get( SupportedAttributes.PERSIST ));
		if( ps == null )
			return null;
		return new PreferenceStore( pluginId);
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		print(e);
		super.error(e);
		//throw( e );
	}

	@Override
	public void fatalError(SAXParseException arg0) throws SAXException {
		print(arg0);
		super.fatalError(arg0);
	}

	@Override
	public void warning(SAXParseException arg0) throws SAXException {
		print(arg0);
		super.warning(arg0);
	}


	private MessageFormat message =
		      new MessageFormat("({0}: {1}, {2}): {3}");
	
	private void print(SAXParseException x)
	   {
	      String msg = message.format(new Object[]
	                                  {
	                                     x.getSystemId(),
	                                     new Integer(x.getLineNumber()),
	                                     new Integer(x.getColumnNumber()),
	                                     x.getMessage()
	                                  });
	      Logger.getLogger( this.getClass().getName()).info(msg);
	   }	
}
