package net.osgi.jp2p.chaupal.xml;

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
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.builder.ComponentNode;
import net.osgi.jp2p.builder.ICompositeBuilder;
import net.osgi.jp2p.builder.ICompositeBuilderListener;
import net.osgi.jp2p.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jp2p.chaupal.advertisement.ChaupalAdvertisementFactory;
import net.osgi.jp2p.chaupal.discovery.ChaupalDiscoveryServiceFactory;
import net.osgi.jp2p.chaupal.pipe.ChaupalPipeFactory;
import net.osgi.jp2p.chaupal.xml.PreferenceStore.Persistence;
import net.osgi.jp2p.chaupal.xml.PreferenceStore.SupportedAttributes;
import net.osgi.jp2p.chaupal.xml.XMLFactoryBuilder.Groups;
import net.osgi.jp2p.container.ContainerFactory;
import net.osgi.jp2p.container.Jp2pContainerPropertySource;
import net.osgi.jp2p.container.Jp2pServiceContainer;
import net.osgi.jp2p.container.IJxseServiceContainer.ContextProperties;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.factory.IComponentFactory.Components;
import net.osgi.jp2p.factory.IJp2pComponents;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementProperties;
import net.osgi.jp2p.jxta.context.JxseContainerPreferences;
import net.osgi.jp2p.jxta.discovery.DiscoveryPreferences;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource;
import net.osgi.jp2p.jxta.discovery.DiscoveryServiceFactory;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory;
import net.osgi.jp2p.jxta.factory.JxtaFactoryUtils;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.jxta.network.INetworkPreferences;
import net.osgi.jp2p.jxta.network.NetworkManagerPreferences;
import net.osgi.jp2p.jxta.network.NetworkManagerPropertySource;
import net.osgi.jp2p.jxta.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jp2p.jxta.network.configurator.NetworkConfigurationFactory;
import net.osgi.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource;
import net.osgi.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jp2p.jxta.network.configurator.OverviewPreferences;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.osgi.jp2p.jxta.pipe.PipePropertySource;
import net.osgi.jp2p.jxta.pipe.PipePropertySource.PipeProperties;
import net.osgi.jp2p.jxta.pipe.PipeServiceFactory;
import net.osgi.jp2p.jxta.registration.RegistrationPropertySource;
import net.osgi.jp2p.jxta.seeds.SeedListPropertySource;
import net.osgi.jp2p.partial.PartialPropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.properties.IJp2pDirectives.Contexts;
import net.osgi.jp2p.seeds.SeedInfo;
import net.osgi.jp2p.utils.IOUtils;
import net.osgi.jp2p.utils.StringDirective;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class XMLFactoryBuilder implements ICompositeBuilder<Jp2pServiceContainer> {

	protected static final String JAXP_SCHEMA_SOURCE =
		    "http://java.sun.com/xml/jaxp/properties/schemaSource";
	protected static final String JP2P_XSD_SCHEMA = "http://www.condast.com/saight/jp2p-schema.xsd";

	private static final String S_ERR_NO_SCHEMA_FOUND = "The XML Schema was not found";
	private static final String S_WRN_NOT_NAMESPACE_AWARE = "The parser is not validating or is not namespace aware";
	
	static final String S_DOCUMENT_ROOT = "DOCUMENT_ROOT";

	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";	
	
	public static String S_DEFAULT_FOLDER = "/JP2P-INF";
	public static String S_DEFAULT_LOCATION = S_DEFAULT_FOLDER + "/jp2p-1.0.0.xml";
	public static String S_SCHEMA_LOCATION =  S_DEFAULT_FOLDER + "/jp2p-schema.xsd";
	
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
	private ContainerBuilder builder;
	private String bundleId;
	
	private Collection<ICompositeBuilderListener<Object>> listeners;
		
	private Logger logger = Logger.getLogger( XMLFactoryBuilder.class.getName() );
	
	public XMLFactoryBuilder( String bundleId, Class<?> clss ) {
		this( bundleId, clss, S_DEFAULT_LOCATION );
	}

	protected XMLFactoryBuilder( String bundleId, Class<?> clss, String location ) {
		this.clss = clss;
		this.bundleId = bundleId;
		this.builder = new ContainerBuilder();
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
	public Jp2pServiceContainer build() {
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
			public void notifyChange(ComponentBuilderEvent<Object> event) {
				notifyListeners(event);
			}
		};
		
		//First parse the XML file
		ContainerFactory root = null;
		try {
			logger.info("Parsing JP2P Bundle: " + this.bundleId );
			//Schema schema = schemaFactory.newSchema(schemaFile);
			//factory.setSchema(schema);//saxParser.
			
			SAXParser saxParser = factory.newSAXParser();
			if( !saxParser.isNamespaceAware() )
				logger.warning( S_WRN_NOT_NAMESPACE_AWARE );
			
			//saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA); 
			//saxParser.setProperty(JAXP_SCHEMA_SOURCE, new File(JP2P_XSD_SCHEMA)); 
			JxtaHandler handler = new JxtaHandler( builder, bundleId );
			saxParser.parse( in, handler);
			root = handler.getRoot();
			logger.info("JP2P Bundle Parsed: " + this.bundleId );
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
		return (Jp2pServiceContainer) root.createComponent();
	}

	/**
	 * Notify that the property sources have been created after parsing the XML
	 * file. This allows for more fine-grained tuning of the property sources
	 * @param node
	 */
	private void extendContainer(){
		IComponentFactory<?>[] factories = this.builder.getChildren();
		for( IComponentFactory<?> factory: factories ){
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
		for( IComponentFactory<?> factory: this.builder.getChildren() ){
			builder.updateRequest( new ComponentBuilderEvent<Object>((IComponentFactory<Object>) factory, BuilderEvents.PROPERTY_SOURCE_CREATED));
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

	private IJp2pComponents current;
	
	private ManagedProperty<IJp2pProperties,Object> property;

	private ContainerBuilder container;
	private ContainerFactory root;
	private FactoryNode node;
	private String bundleId;

	private static Logger logger = Logger.getLogger( XMLFactoryBuilder.class.getName() );

	public JxtaHandler( ContainerBuilder container, String bundleId ) {
		this.bundleId = bundleId;
		this.container = container;
	}

	ContainerFactory getRoot() {
		return root;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, 
			Attributes attributes) throws SAXException {
		IComponentFactory<?> factory = null;
		if( Components.isComponent( qName )){
			current = Components.valueOf( StringStyler.styleToEnum( qName ));
			switch(( Components )current ){
			case JP2P_CONTAINER:
				factory = new ContainerFactory( container, bundleId );
				this.root = (ContainerFactory) factory;
				break;
			default:
				factory = container.getDefaultFactory( node.getData().getPropertySource(), current.toString());
				break;
			}
			node = this.processFactory(attributes, node, factory);
			return;
		}else if( IJxtaComponentFactory.JxtaComponents.isComponent( qName )){
			current = IJxtaComponentFactory.JxtaComponents.valueOf( StringStyler.styleToEnum( qName ));
			factory = JxtaFactoryUtils.getDefaultFactory(container, node.getData().getPropertySource(), current.toString());
			node = this.processFactory(attributes, node, factory);
			return;
		}if( Groups.isGroup( qName )){
			logger.info(" Group value: " + qName );
			return;
		}else{
			this.property = this.createProperty(qName, attributes );
		}
	}

	/**
	 * Process the factory by adding the directives and adding it to the container
	 * @param attributes
	 * @param parent
	 * @param factory
	 * @return
	 */
	protected FactoryNode processFactory( Attributes attributes, FactoryNode parent, IComponentFactory<?> factory ){
		IJp2pWritePropertySource<?> source = (IJp2pWritePropertySource<?>) factory.createPropertySource();
		if( parent != null )
			parent.getData().getPropertySource().addChild( source);
		
		//Add the directives
		if( source instanceof IJp2pWritePropertySource ){
			source = (IJp2pWritePropertySource<?>) factory.getPropertySource();
			for( int i=0; i<attributes.getLength(); i++  ){
				if( !Utils.isNull( attributes.getLocalName(i))){
					IJp2pDirectives directive = new StringDirective( StringStyler.styleToEnum( attributes.getLocalName(i))); 
					source.setDirective( directive, attributes.getValue(i));
				}
			}
		}
		container.addFactory( getChaupalFactory( factory ));
		if( node == null )
			return new FactoryNode( factory );
		else
			return (FactoryNode) node.addChild(factory);	
	}
	/**
	 * Change the factory to a Chaupal factory if required and available
	 * @param factory
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected IComponentFactory<?> getChaupalFactory( IComponentFactory<?> base ){
		Contexts context = Jp2pContainerPropertySource.getContext( base.getPropertySource() );
		IComponentFactory<?> factory = base;
		switch( context ){
		case CHAUPAL:
			String str = StringStyler.styleToEnum(base.getComponentName());
			if(! IJxtaComponentFactory.JxtaComponents.isComponent(str))
				break;
			IJxtaComponentFactory.JxtaComponents comp = IJxtaComponentFactory.JxtaComponents.valueOf(str );
			switch( comp ){
			case ADVERTISEMENT_SERVICE:
				factory = new ChaupalAdvertisementFactory<Advertisement>(container, (IComponentFactory<Advertisement>)base);
				factory.createPropertySource();
				break;
			case DISCOVERY_SERVICE:
				factory = new ChaupalDiscoveryServiceFactory( container, (DiscoveryServiceFactory)base );
				factory.createPropertySource();
				break;
			case PIPE_SERVICE:
				factory = new ChaupalPipeFactory(container, (PipeServiceFactory) base);
				factory.createPropertySource();
				break;
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
	protected ManagedProperty<IJp2pProperties,Object> createProperty( String qName, Attributes attributes ){
		String id = StringStyler.styleToEnum( qName );
		Object value = null;
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) node.getData().getPropertySource();
		if( isCreated(attributes)){
			if( source instanceof NetworkConfigurationPropertySource ){
				OverviewPreferences preferences = new OverviewPreferences( source );
				value = preferences.createDefaultValue((NetworkConfiguratorProperties) source.getIdFromString( id ));
			}
		}
		ManagedProperty<IJp2pProperties,Object> prop = source.getOrCreateManagedProperty( source.getIdFromString( id ), value, false);
		for( int i=0; i<attributes.getLength(); i++  ){
			if( !Utils.isNull( attributes.getLocalName(i)))
				prop.addAttribute(attributes.getLocalName(i), attributes.getValue(i));
		}
		return prop;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if( !Components.isComponent( qName ) && !JxtaComponents.isComponent(qName ))
			return;
		node = (FactoryNode) node.getParent();
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
		
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) node.getData().getPropertySource();
		if( source instanceof Jp2pContainerPropertySource ){
			JxseContainerPreferences preferences = new JxseContainerPreferences( (Jp2pContainerPropertySource) source );
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
			pps.setProperty( (PipeProperties) property.getKey(), value);
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
			SeedInfo seedInfo = new SeedInfo((( IJp2pProperties )property.getKey()).name(), ( String )value );
			slps.setProperty( (IJp2pProperties) property.getKey(), seedInfo );
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

class FactoryNode extends ComponentNode<IComponentFactory<Object>>{

	@SuppressWarnings("unchecked")
	protected FactoryNode(IComponentFactory<?> data, FactoryNode parent) {
		super((IComponentFactory<Object>) data, parent);
	}

	@SuppressWarnings("unchecked")
	public FactoryNode(IComponentFactory<?> data) {
		super((IComponentFactory<Object>) data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComponentNode<?> addChild(Object data) {
		FactoryNode child = new FactoryNode( (IComponentFactory<Object>) data, this );
		super.getChildrenAsCollection().add(child);
		return child;
	}

	
}