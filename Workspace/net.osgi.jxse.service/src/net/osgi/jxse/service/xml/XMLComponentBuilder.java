package net.osgi.jxse.service.xml;

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
import net.osgi.jxse.advertisement.AdvertisementPropertySource;
import net.osgi.jxse.builder.CompositeBuilder;
import net.osgi.jxse.builder.ICompositeBuilder;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.context.JxseContextPreferences;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.discovery.DiscoveryPreferences;
import net.osgi.jxse.discovery.DiscoveryPropertySource;
import net.osgi.jxse.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jxse.factory.ComponentFactoryEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.network.INetworkPreferences;
import net.osgi.jxse.network.NetworkConfigurationFactory;
import net.osgi.jxse.network.NetworkConfigurationPropertySource;
import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jxse.network.NetworkManagerPreferences;
import net.osgi.jxse.network.NetworkManagerPropertySource;
import net.osgi.jxse.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jxse.properties.CategoryPropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.properties.PartialPropertySource;
import net.osgi.jxse.properties.SeedListPropertySource;
import net.osgi.jxse.peergroup.PeerGroupPropertySource;
import net.osgi.jxse.registration.RegistrationPreferences;
import net.osgi.jxse.registration.RegistrationPropertySource;
import net.osgi.jxse.registration.RegistrationPropertySource.RegistrationProperties;
import net.osgi.jxse.service.xml.PreferenceStore.Persistence;
import net.osgi.jxse.service.xml.PreferenceStore.SupportedAttributes;
import net.osgi.jxse.service.xml.XMLComponentBuilder.Groups;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;
import net.osgi.jxse.utils.io.IOUtils;

public class XMLComponentBuilder implements IComponentFactory<NetworkManager, ContextProperties, IJxseDirectives.Directives>, ICompositeBuilder<NetworkManager> {

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
		SEED_LIST;

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
	
	private Logger logger = Logger.getLogger( XMLComponentBuilder.class.getName() );
	
	public XMLComponentBuilder( String pluginId, Class<?> clss) {
		this( pluginId, clss, S_DEFAULT_LOCATION );
	}

	protected XMLComponentBuilder( String pluginId, Class<?> clss, String location ) {
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
		return Boolean.parseBoolean( (String) properties.getDirective( IJxseDirectives.Directives.AUTO_START ));
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
			listener.notifyCreated(event);
	}

	@Override
	public boolean canCreate(){
		if( clss == null )
			return false;
		return ( clss.getResourceAsStream( location ) != null );
	}

	
	@Override
	public NetworkManager build() {
		return this.createModule();
	}

	@Override
	public NetworkManager createModule() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		URL schema_in = XMLComponentBuilder.class.getResource( S_SCHEMA_LOCATION); 
		if( schema_in == null )
			throw new RuntimeException( S_ERR_NO_SCHEMA_FOUND );
		
		//factory.setNamespaceAware( true );
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// note that if your XML already declares the XSD to which it has to conform, then there's no need to create a validator from a Schema object
		Source schemaFile = new StreamSource( JxtaHandler.class.getResourceAsStream( S_SCHEMA_LOCATION ));
		InputStream in = clss.getResourceAsStream( location );
		ICompositeBuilderListener listener = new ICompositeBuilderListener(){

			@Override
			public void notifyCreated(ComponentFactoryEvent event) {
				notifyListeners(event);
			}
		};
		ICompositeBuilder<NetworkManager> cf = new CompositeBuilder<NetworkManager, ContextProperties, IJxseDirectives.Directives>( this.getPropertySource() );
		cf.addListener( listener );
		try {
			logger.info("Parsing JXSE Bundle: " + this.properties.getProperty( ContextProperties.BUNDLE_ID ));
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
			module = cf.build();
			logger.info("JXSE Bundle Parsed: " + this.properties.getProperty( ContextProperties.BUNDLE_ID ));
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
			cf.removeListener(listener);
		}
		return module;
	}

	@Override
	public boolean complete() {
		this.completed = true;
		return completed;
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
	public IJxsePropertySource<ContextProperties, IJxseDirectives.Directives> getPropertySource() {
		return this.properties;
	}
	
	public static String getLocation( String defaultLocation ){
		if( !Utils.isNull( defaultLocation ))
			return defaultLocation;
		return defaultLocation;
	}
}

class JxtaHandler extends DefaultHandler{

	private XMLComponentBuilder owner;
	private Components current;
	
	@SuppressWarnings("rawtypes")
	private IJxseWritePropertySource source;
	private ManagedProperty<Object,Object> property;


	private static Logger logger = Logger.getLogger( XMLComponentBuilder.class.getName() );

	public JxtaHandler( XMLComponentBuilder owner ) {
		super();
		this.owner = owner;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void startElement(String uri, String localName, String qName, 
			Attributes attributes) throws SAXException {

		if( Components.isComponent( qName )){

			current = Components.valueOf( StringStyler.styleToEnum( qName ));
			IJxseWritePropertySource<?,?> newSource;
			switch( current ){
			case JXSE_CONTEXT:
				newSource = (IJxseWritePropertySource) owner.getPropertySource();
				break;
			case NETWORK_MANAGER:
				newSource = new NetworkManagerPropertySource( (JxseContextPropertySource) source );
				break;
			case NETWORK_CONFIGURATOR:
				newSource = new NetworkConfigurationPropertySource((NetworkManagerPropertySource)source );
				break;
			case SEED_LIST:
				newSource = new SeedListPropertySource((NetworkConfigurationPropertySource)source );
				break;
			case TCP:
			case HTTP:
			case HTTP2:
			case MULTICAST:
			case SECURITY:
				newSource = new PartialPropertySource( qName, source );
				break;
			case ADVERTISEMENT:
				newSource = new AdvertisementPropertySource( qName, source );
				break;			
			case REGISTRATION_SERVICE:
				newSource = new RegistrationPropertySource( qName, source );
				break;
			case DISCOVERY_SERVICE:
				newSource = new DiscoveryPropertySource( qName, source );
				break;			
			case PEERGROUP_SERVICE:
				newSource = new PeerGroupPropertySource( qName, source );
				break;			
			default:
				newSource = new CategoryPropertySource( qName, source );
				break;
			}
			if( source != null )
				source.addChild( newSource );
			source = newSource;
			
			for( int i=0; i<attributes.getLength(); i++  ){
				if( !Utils.isNull( attributes.getLocalName(i))){
					String attr = StringStyler.styleToEnum( attributes.getLocalName(i)); 
					source.setDirective( source.getDirectiveFromString( attr ), attributes.getValue(i));
				}
			}
		}else if( !Groups.isGroup( qName )){
			String id = StringStyler.styleToEnum( qName );
			property = source.getOrCreateManagedProperty( source.getIdFromString( id ), null, false);
			for( int i=0; i<attributes.getLength(); i++  ){
				if( !Utils.isNull( attributes.getLocalName(i)))
					property.addAttribute(attributes.getLocalName(i), attributes.getValue(i));
			}
		}
		logger.info(" Group value: " + qName );
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if( Components.isComponent( qName )){
			this.source = (IJxseWritePropertySource) source.getParent();
		}
		this.property = null;
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		this.parseProperties(ch, start, length);
	}

	/**
	 * Parse the properties
	 * @param ch
	 * @param start
	 * @param length
	 * @throws SAXException
	 */
	@SuppressWarnings({ "unchecked" })
	protected void parseProperties(char ch[], int start, int length) throws SAXException {
		if(( this.property == null ) || ( property.getKey() == null ))
			return;
		
		String value = new String(ch, start, length);
		if( Utils.isNull( value  ))
			return;

		if( source instanceof JxseContextPropertySource ){
			JxseContextPreferences preferences = new JxseContextPreferences( source );
			preferences.setPropertyFromString((ContextProperties) property.getKey(), value);
			return;
		}
		if( source instanceof NetworkManagerPropertySource ){
			NetworkManagerPreferences<IJxseDirectives> preferences = new NetworkManagerPreferences<IJxseDirectives>( source );
			preferences.setPropertyFromString( (NetworkManagerProperties) property.getKey(), value);
			return;
		}
		if( source instanceof DiscoveryPropertySource ){
			DiscoveryPreferences<IJxseDirectives> preferences = new DiscoveryPreferences<IJxseDirectives>( source );
			preferences.setPropertyFromString(( DiscoveryProperties) property.getKey(), value);
			return;
		}
		if( source instanceof PeerGroupPropertySource ){
			//DiscoveryPreferences<IJxseDirectives> preferences = new DiscoveryPreferences<IJxseDirectives>( source );
			//preferences.setPropertyFromString(( DiscoveryProperties) property.getKey(), value);
			return;
		}
		if( source instanceof RegistrationPropertySource ){
			RegistrationPreferences<IJxseDirectives> preferences = new RegistrationPreferences<IJxseDirectives>( source );
			preferences.setPropertyFromString(( RegistrationProperties) property.getKey(), value);
			return;
		}
		if( source instanceof PartialPropertySource ){
			INetworkPreferences preferences = NetworkConfigurationFactory.getPreferences((PartialPropertySource<NetworkConfiguratorProperties, IJxseDirectives>) source);
			if( preferences != null )
				preferences.setPropertyFromString( (NetworkConfiguratorProperties) property.getKey(), value);
			return;
		}
		if( source instanceof SeedListPropertySource ){
			SeedListPropertySource<?> slps = (SeedListPropertySource<?>) source;
			slps.setProperty((String) property.getKey(), value);
			return;
		}
		this.property.setValue(value);
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
