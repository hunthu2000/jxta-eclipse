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
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementProperties;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jxse.advertisement.PipeAdvertisementPropertySource;
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.ICompositeBuilder;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.context.JxseContextPreferences;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.discovery.DiscoveryPreferences;
import net.osgi.jxse.discovery.DiscoveryPropertySource;
import net.osgi.jxse.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.network.INetworkPreferences;
import net.osgi.jxse.network.NetworkConfigurationFactory;
import net.osgi.jxse.network.NetworkConfigurationPropertySource;
import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jxse.network.NetworkManagerPreferences;
import net.osgi.jxse.network.NetworkManagerPropertySource;
import net.osgi.jxse.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jxse.network.OverviewPreferences;
import net.osgi.jxse.properties.CategoryPropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.properties.PartialPropertySource;
import net.osgi.jxse.peergroup.PeerGroupPropertySource;
import net.osgi.jxse.registration.RegistrationPreferences;
import net.osgi.jxse.registration.RegistrationPropertySource;
import net.osgi.jxse.registration.RegistrationPropertySource.RegistrationProperties;
import net.osgi.jxse.seeds.SeedInfo;
import net.osgi.jxse.seeds.SeedListPropertySource;
import net.osgi.jxse.service.core.JxseCompositeBuilder;
import net.osgi.jxse.service.xml.PreferenceStore.Persistence;
import net.osgi.jxse.service.xml.PreferenceStore.SupportedAttributes;
import net.osgi.jxse.service.xml.XMLFactoryBuilder.Groups;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;
import net.osgi.jxse.utils.io.IOUtils;

public class XMLFactoryBuilder implements ICompositeBuilder<NetworkManager, ContextProperties, IJxseDirectives> {

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
	
	Class<?> clss;
	private boolean completed, failed;
	private JxseContextPropertySource properties;
	private String location;
	
	private Collection<ICompositeBuilderListener<Object>> listeners;
	
	private ComponentNode<NetworkManager, ContextProperties, IJxseDirectives> module;
	
	private Logger logger = Logger.getLogger( XMLFactoryBuilder.class.getName() );
	
	public XMLFactoryBuilder( String pluginId, Class<?> clss) {
		this( pluginId, clss, S_DEFAULT_LOCATION );
	}

	protected XMLFactoryBuilder( String pluginId, Class<?> clss, String location ) {
		this.clss = clss;
		this.location = location;
		this.completed = false;
		this.failed = false;
		properties = new JxseContextPropertySource( pluginId, location);
		this.listeners = new ArrayList<ICompositeBuilderListener<Object>>();
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
	@SuppressWarnings("unchecked")
	@Override
	public void addListener( ICompositeBuilderListener<?> listener ){
		this.listeners.add((ICompositeBuilderListener<Object>) listener);
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#removeListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	@Override
	public void removeListener( ICompositeBuilderListener<?> listener ){
		this.listeners.remove( listener);
	}

	void notifyListeners( ComponentBuilderEvent<Object> event ){
		for( ICompositeBuilderListener<Object> listener: listeners )
			listener.notifyCreated(event);
	}

	public boolean canCreate(){
		if( clss == null )
			return false;
		return ( clss.getResourceAsStream( location ) != null );
	}

	
	@Override
	public ComponentNode<NetworkManager,ContextProperties, IJxseDirectives> build() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		URL schema_in = XMLFactoryBuilder.class.getResource( S_SCHEMA_LOCATION); 
		if( schema_in == null )
			throw new RuntimeException( S_ERR_NO_SCHEMA_FOUND );
		
		//factory.setNamespaceAware( true );
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// note that if your XML already declares the XSD to which it has to conform, then there's no need to create a validator from a Schema object
		Source schemaFile = new StreamSource( JxtaHandler.class.getResourceAsStream( S_SCHEMA_LOCATION ));
		InputStream in = clss.getResourceAsStream( location );
		ICompositeBuilderListener<?> listener = new ICompositeBuilderListener<Object>(){

			@Override
			public void notifyCreated(ComponentBuilderEvent<Object> event) {
				notifyListeners(event);
			}
		};
		
		//First parse the XML file
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
			handler.addListener(listener);
			saxParser.parse( in, handler);
			handler.removeListener(listener);
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

		//Then build the factories
		ICompositeBuilder<NetworkManager, ContextProperties, IJxseDirectives> cf = new JxseCompositeBuilder<NetworkManager, ContextProperties, IJxseDirectives>( this.getPropertySource() );
		cf.addListener( listener );
		module = cf.build();
		cf.removeListener(listener);
		logger.info("JXSE Bundle Parsed: " + this.properties.getProperty( ContextProperties.BUNDLE_ID ));
		this.completed = true;
		return module;
	}

	public boolean complete() {
		this.completed = true;
		return completed;
	}

	public boolean isCompleted() {
		return completed;
	}

	public boolean hasFailed() {
		return failed;
	}

	public IJxsePropertySource<ContextProperties, IJxseDirectives> getPropertySource() {
		return this.properties;
	}
	
	public static String getLocation( String defaultLocation ){
		if( !Utils.isNull( defaultLocation ))
			return defaultLocation;
		return defaultLocation;
	}
}

class JxtaHandler extends DefaultHandler{

	private XMLFactoryBuilder owner;
	private Components current;
	
	@SuppressWarnings("rawtypes")
	private IJxseWritePropertySource source;
	private ManagedProperty<Object,Object> property;

	private Collection<IJxseModule<?,?,?>> modules;
	
	private Collection<ICompositeBuilderListener<Object>> listeners;
	
	private static Logger logger = Logger.getLogger( XMLFactoryBuilder.class.getName() );

	public JxtaHandler( XMLFactoryBuilder owner ) {
		this.owner = owner;
		modules = new ArrayList<IJxseModule<?,?,?>>();
		listeners = new ArrayList<ICompositeBuilderListener<Object>>();
	}

	@SuppressWarnings("unchecked")
	void addListener( ICompositeBuilderListener<?> listener ){
		this.listeners.add( (ICompositeBuilderListener<Object>) listener);
	}

	void removeListener( ICompositeBuilderListener<?> listener ){
		this.listeners.remove( listener);
	}

	private void notifyListeners( ComponentBuilderEvent<Object> event ){
		for( ICompositeBuilderListener<Object> listener: this.listeners )
			listener.notifyCreated( event);
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
				SeedListPropertySource slps = new SeedListPropertySource( newSource, owner.clss );
				if( slps.hasSeeds() )
					newSource.addChild(slps);
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
			case ADVERTISEMENT_SERVICE:
				AdvertisementTypes type = this.getAdvertisementType(attributes, qName, source );
				//this.modules.add( new AdvertisementModule( qName, type, source ));
				newSource = this.getAdvertisementPropertysource(attributes, qName, source );
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
			case EXTENDED:
				newSource = new PartialPropertySource( qName, source );
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
			this.property = this.createProperty(qName, attributes );
		}
		logger.info(" Group value: " + qName );
	}

	/**
	 * Get the correct advertisement type
	 * @param attrs
	 * @param qName
	 * @param parent
	 * @return
	 */
	protected AdvertisementTypes getAdvertisementType( Attributes attrs, String qName ,IJxsePropertySource<IJxseProperties, IJxseDirectives> parent ){
		if(( attrs == null ) || ( attrs.getLength() == 0))
				return AdvertisementTypes.ADV;
		String type = attrs.getValue(AdvertisementDirectives.TYPE.toString().toLowerCase() );
		if( Utils.isNull(type))
			return AdvertisementTypes.ADV;
		return AdvertisementTypes.valueOf( StringStyler.styleToEnum( type ));
	}

	/**
	 * Get the correct property source
	 * @param attrs
	 * @param qName
	 * @param parent
	 * @return
	 */
	protected AdvertisementPropertySource getAdvertisementPropertysource( Attributes attrs, String qName ,IJxsePropertySource<IJxseProperties, IJxseDirectives> parent ){
		AdvertisementPropertySource source = new AdvertisementPropertySource( qName, parent );
		AdvertisementTypes adv_type = this.getAdvertisementType(attrs, qName, parent);
		switch( adv_type ){
		case PIPE:
			return new PipeAdvertisementPropertySource( source );
		default:
			return source;
		}
	}
	

	/**
	 * Create the property
	 * @param qName
	 * @param attributes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected ManagedProperty<Object,Object> createProperty( String qName, Attributes attributes ){
		String id = StringStyler.styleToEnum( qName );
		Object value = null;
		if( isCreated(attributes)){
			if( source instanceof NetworkConfigurationPropertySource ){
				OverviewPreferences preferences = new OverviewPreferences( source );
				value = preferences.createDefaultValue((NetworkConfiguratorProperties) source.getIdFromString( id ));
			}
		}
		ManagedProperty<Object,Object> prop = source.getOrCreateManagedProperty( source.getIdFromString( id ), value, false);
		for( int i=0; i<attributes.getLength(); i++  ){
			if( !Utils.isNull( attributes.getLocalName(i)))
				prop.addAttribute(attributes.getLocalName(i), attributes.getValue(i));
		}
		return prop;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if( Components.isComponent( qName )){
			this.notifyListeners( new ComponentBuilderEvent( this, this.source, BuilderEvents.PROPERTY_SOURCE_CREATED ));
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
		String value = new String(ch, start, length);
		if( Utils.isNull( value  ))
			return;

		if(( property == null ) || ( property.getKey() == null ))
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
		if( source instanceof NetworkConfigurationPropertySource ){
			OverviewPreferences preferences = new OverviewPreferences( source );
			preferences.setPropertyFromString( (NetworkConfiguratorProperties) property.getKey(), value);
			return;
		}
		if( source instanceof DiscoveryPropertySource ){
			DiscoveryPreferences<IJxseDirectives> preferences = new DiscoveryPreferences<IJxseDirectives>( source );
			preferences.setPropertyFromString(( DiscoveryProperties) property.getKey(), value);
			return;
		}
		if( source instanceof AdvertisementPropertySource ){
			AdvertisementPropertySource aps = ( AdvertisementPropertySource )source ;
			aps.setProperty( (AdvertisementProperties) property.getKey(), value);
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
			SeedInfo seedInfo = new SeedInfo((String) property.getKey(), ( String )value );
			slps.setProperty((String) property.getKey(), seedInfo );
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
	
	/**
	 * Returns true if one of the attributes allows creation of values
	 * @param attributes
	 * @return
	 */
	protected static boolean isCreated( Attributes attributes ){
		String attr = attributes.getValue( ManagedProperty.Attributes.PERSIST.name().toLowerCase() );
		if( Utils.isNull(attr))
			return false;
		return Boolean.parseBoolean( attr );
	}
}
