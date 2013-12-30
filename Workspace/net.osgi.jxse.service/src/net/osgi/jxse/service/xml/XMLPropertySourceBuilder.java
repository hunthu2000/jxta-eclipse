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

import net.jxta.document.Advertisement;
import net.osgi.jxse.advertisement.AdvertisementPropertySource;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementProperties;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.builder.ICompositeBuilder;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jxse.context.ContextFactory;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.context.JxseContextPreferences;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.discovery.DiscoveryPreferences;
import net.osgi.jxse.discovery.DiscoveryPropertySource;
import net.osgi.jxse.discovery.DiscoveryServiceFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.network.INetworkPreferences;
import net.osgi.jxse.network.NetworkManagerPreferences;
import net.osgi.jxse.network.NetworkManagerPropertySource;
import net.osgi.jxse.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jxse.network.configurator.NetworkConfigurationFactory;
import net.osgi.jxse.network.configurator.NetworkConfigurationPropertySource;
import net.osgi.jxse.network.configurator.OverviewPreferences;
import net.osgi.jxse.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseDirectives.Contexts;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.partial.PartialPropertySource;
import net.osgi.jxse.peergroup.PeerGroupPropertySource;
import net.osgi.jxse.pipe.PipePropertySource;
import net.osgi.jxse.pipe.PipeServiceFactory;
import net.osgi.jxse.registration.RegistrationPropertySource;
import net.osgi.jxse.seeds.SeedInfo;
import net.osgi.jxse.seeds.SeedListPropertySource;
import net.osgi.jxse.service.advertisement.ChaupalAdvertisementFactory;
import net.osgi.jxse.service.discovery.ChaupalDiscoveryServiceFactory;
import net.osgi.jxse.service.pipe.ChaupalPipeFactory;
import net.osgi.jxse.service.xml.PreferenceStore.Persistence;
import net.osgi.jxse.service.xml.PreferenceStore.SupportedAttributes;
import net.osgi.jxse.service.xml.XMLPropertySourceBuilder.Groups;
import net.osgi.jxse.utils.StringDirective;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;
import net.osgi.jxse.utils.io.IOUtils;

public class XMLPropertySourceBuilder implements ICompositeBuilder<ContextFactory> {

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
	private String location;
	private BuilderContainer container;
	private String bundleId;
	
	private Collection<ICompositeBuilderListener<Object>> listeners;
		
	private Logger logger = Logger.getLogger( XMLPropertySourceBuilder.class.getName() );
	
	public XMLPropertySourceBuilder( String bundleId, Class<?> clss, BuilderContainer container ) {
		this( bundleId, clss, S_DEFAULT_LOCATION, container );
	}

	protected XMLPropertySourceBuilder( String bundleId, Class<?> clss, String location, BuilderContainer container ) {
		this.clss = clss;
		this.bundleId = bundleId;
		this.container = container;
		this.location = location;
		this.completed = false;
		this.failed = false;
		this.listeners = new ArrayList<ICompositeBuilderListener<Object>>();
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
			listener.notifyChange(event);
	}

	public boolean canCreate(){
		if( clss == null )
			return false;
		return ( clss.getResourceAsStream( location ) != null );
	}

	
	@Override
	public ContextFactory build() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		URL schema_in = XMLPropertySourceBuilder.class.getResource( S_SCHEMA_LOCATION); 
		if( schema_in == null )
			throw new RuntimeException( S_ERR_NO_SCHEMA_FOUND );
		
		//factory.setNamespaceAware( true );
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// note that if your XML already declares the XSD to which it has to conform, then there's no need to create a validator from a Schema object
		Source schemaFile = new StreamSource( JxtaHandler.class.getResourceAsStream( S_SCHEMA_LOCATION ));
		InputStream in = clss.getResourceAsStream( location );
		ICompositeBuilderListener<?> listener = new ICompositeBuilderListener<Object>(){

			@Override
			public void notifyChange(ComponentBuilderEvent<Object> event) {
				notifyListeners(event);
			}
		};
		
		//First parse the XML file
		ContextFactory root = null;
		try {
			logger.info("Parsing JXSE Bundle: " + this.bundleId );
			//Schema schema = schemaFactory.newSchema(schemaFile);
			//factory.setSchema(schema);//saxParser.
			
			SAXParser saxParser = factory.newSAXParser();
			if( !saxParser.isNamespaceAware() )
				logger.warning( S_WRN_NOT_NAMESPACE_AWARE );
			
			//saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA); 
			//saxParser.setProperty(JAXP_SCHEMA_SOURCE, new File(JXSE_XSD_SCHEMA)); 
			JxtaHandler handler = new JxtaHandler( container, bundleId );
			saxParser.parse( in, handler);
			root = handler.getRoot();
			logger.info("JXSE Bundle Parsed: " + this.bundleId );
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
		
		//Extend the container with factories that are also needed
		this.extendContainer();
		this.notifyPropertyCreated();
		this.completed = true;
		return root;
	}

	/**
	 * Notify that the property sources have been created after parsing the XML
	 * file. This allows for more fine-grained tuning of the property sources
	 * @param node
	 */
	private void extendContainer(){
		for( IComponentFactory<?> factory: this.container.getChildren() ){
			factory.extendContainer();
		}
	}

	/**
	 * Notify that the property sources have been created after parsing the XML
	 * file. This allows for more fine-grained tuning of the property sources
	 * @param node
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void notifyPropertyCreated(){
		for( IComponentFactory<?> factory: this.container.getChildren() ){
			container.updateRequest( new ComponentBuilderEvent<Object>((IComponentFactory<Object>) factory, BuilderEvents.PROPERTY_SOURCE_CREATED));
			this.notifyListeners( new ComponentBuilderEvent( factory, BuilderEvents.PROPERTY_SOURCE_CREATED ));
		}
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

	public static String getLocation( String defaultLocation ){
		if( !Utils.isNull( defaultLocation ))
			return defaultLocation;
		return defaultLocation;
	}
}

class JxtaHandler extends DefaultHandler{

	private Components current;
	
	private ManagedProperty<IJxseProperties,Object> property;

	private BuilderContainer container;
	private ContextFactory root;
	private IComponentFactory<?> curfactory;
	private String bundleId;

	private static Logger logger = Logger.getLogger( XMLPropertySourceBuilder.class.getName() );

	public JxtaHandler( BuilderContainer container, String bundleId ) {
		this.bundleId = bundleId;
		this.container = container;
	}

	ContextFactory getRoot() {
		return root;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, 
			Attributes attributes) throws SAXException {
		IComponentFactory<?> factory = null;
		if( Components.isComponent( qName )){
			current = Components.valueOf( StringStyler.styleToEnum( qName ));
			IJxseWritePropertySource<?> source = null;
			switch( current ){
			case JXSE_CONTEXT:
				factory = new ContextFactory( container, bundleId );
				source = (IJxseWritePropertySource<?>) factory.createPropertySource();
				this.root = (ContextFactory) factory;
				break;
			default:
				factory = container.getDefaultFactory( curfactory.getPropertySource(), current.name());
				source = (IJxseWritePropertySource<?>) factory.createPropertySource();
				curfactory.getPropertySource().addChild( source);
				break;
			}
			//Add the directives
			if( source instanceof IJxseWritePropertySource ){
				source = (IJxseWritePropertySource<?>) factory.getPropertySource();
				for( int i=0; i<attributes.getLength(); i++  ){
					if( !Utils.isNull( attributes.getLocalName(i))){
						IJxseDirectives directive = new StringDirective( StringStyler.styleToEnum( attributes.getLocalName(i))); 
						source.setDirective( directive, attributes.getValue(i));
					}
				}
			}
			container.addFactory( getChaupalFactory( factory ));
			curfactory = factory;
		}else if( !Groups.isGroup( qName )){
			this.property = this.createProperty(qName, attributes );
		}
		logger.info(" Group value: " + qName );
	}

	/**
	 * Change the factory to a Chaupal factory if required and available
	 * @param factory
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected IComponentFactory<?> getChaupalFactory( IComponentFactory<?> factory ){
		Contexts context = JxseContextPropertySource.getContext( factory.getPropertySource() );
		switch( context ){
		case CHAUPAL:
			String str = StringStyler.styleToEnum(factory.getComponentName());
			if(! Components.isComponent(str))
				break;
			Components comp = Components.valueOf(str );
			switch( comp ){
			case ADVERTISEMENT_SERVICE:
				return new ChaupalAdvertisementFactory(container, (IComponentFactory<Advertisement>) factory);
			case DISCOVERY_SERVICE:
				return new ChaupalDiscoveryServiceFactory( container, (DiscoveryServiceFactory) factory );
			case PIPE_SERVICE:
				return new ChaupalPipeFactory(container, (PipeServiceFactory) factory);
			default:
				break;
			}
		default:
			break;
		}
		return factory;
	}

	/**
	 * Create the property
	 * @param qName
	 * @param attributes
	 * @return
	 */
	protected ManagedProperty<IJxseProperties,Object> createProperty( String qName, Attributes attributes ){
		String id = StringStyler.styleToEnum( qName );
		Object value = null;
		IJxseWritePropertySource<IJxseProperties> source = (IJxseWritePropertySource<IJxseProperties>) curfactory.getPropertySource();
		if( isCreated(attributes)){
			if( source instanceof NetworkConfigurationPropertySource ){
				OverviewPreferences preferences = new OverviewPreferences( source );
				value = preferences.createDefaultValue((NetworkConfiguratorProperties) source.getIdFromString( id ));
			}
		}
		ManagedProperty<IJxseProperties,Object> prop = source.getOrCreateManagedProperty( source.getIdFromString( id ), value, false);
		for( int i=0; i<attributes.getLength(); i++  ){
			if( !Utils.isNull( attributes.getLocalName(i)))
				prop.addAttribute(attributes.getLocalName(i), attributes.getValue(i));
		}
		return prop;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if( !Components.isComponent( qName ) || ( curfactory == null ) || ( curfactory.getPropertySource().getParent() == null ))
			return;
		String parentcomp = curfactory.getPropertySource().getParent().getComponentName();
		curfactory = container.getFactory( parentcomp);
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
	protected void parseProperties(char ch[], int start, int length) throws SAXException {
		String value = new String(ch, start, length);
		if( Utils.isNull( value  ))
			return;

		if(( property == null ) || ( property.getKey() == null ))
			return;
		
		IJxseWritePropertySource<IJxseProperties> source = (IJxseWritePropertySource<IJxseProperties>) curfactory.getPropertySource();
		if( source instanceof JxseContextPropertySource ){
			JxseContextPreferences preferences = new JxseContextPreferences( (JxseContextPropertySource) source );
			preferences.setPropertyFromString((ContextProperties) property.getKey(), value);
			return;
		}
		if( source instanceof NetworkManagerPropertySource ){
			NetworkManagerPreferences preferences = new NetworkManagerPreferences( source );
			preferences.setPropertyFromString( (NetworkManagerProperties) property.getKey(), value);
			return;
		}
		if( source instanceof NetworkConfigurationPropertySource ){
			OverviewPreferences preferences = new OverviewPreferences( source );
			preferences.setPropertyFromString( (NetworkConfiguratorProperties) property.getKey(), value);
			return;
		}
		if( source instanceof DiscoveryPropertySource ){
			DiscoveryPreferences preferences = new DiscoveryPreferences( source );
			preferences.setPropertyFromString( property.getKey(), value);
			return;
		}
		if( source instanceof AdvertisementPropertySource ){
			AdvertisementPropertySource aps = ( AdvertisementPropertySource )source ;
			aps.setProperty( (AdvertisementProperties) property.getKey(), value);
			return;
		}
		if( source instanceof PipePropertySource ){
			PipePropertySource pps = ( PipePropertySource )source ;
			//pps.setPropertyFromString( (PipeProperties) property.getKey(), value);
			return;
		}
		if( source instanceof PeerGroupPropertySource ){
			//DiscoveryPreferences<IJxseDirectives> preferences = new DiscoveryPreferences<IJxseDirectives>( source );
			//preferences.setPropertyFromString(( DiscoveryProperties) property.getKey(), value);
			return;
		}
		if( source instanceof RegistrationPropertySource ){
			//RegistrationPreferences preferences = new RegistrationPreferences( source );
			//preferences.setPropertyFromString(( RegistrationProperties) property.getKey(), value);
			return;
		}
		if( source instanceof PartialPropertySource ){
			INetworkPreferences preferences = NetworkConfigurationFactory.getPreferences((PartialPropertySource) source);
			if( preferences != null )
				preferences.setPropertyFromString( (NetworkConfiguratorProperties) property.getKey(), value);
			return;
		}
		if( source instanceof SeedListPropertySource ){
			SeedListPropertySource slps = (SeedListPropertySource) source;
			SeedInfo seedInfo = new SeedInfo((( IJxseProperties )property.getKey()).name(), ( String )value );
			slps.setProperty( (IJxseProperties) property.getKey(), seedInfo );
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
