package net.jp2p.chaupal.xml;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jp2p.container.ContainerFactory;
import net.jp2p.container.Jp2pContainerPropertySource;
import net.jp2p.container.builder.ComponentNode;
import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.context.ContextLoader;
import net.jp2p.container.context.IJp2pContext;
import net.jp2p.container.context.Jp2pContainerPreferences;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.factory.IComponentFactory;
import net.jp2p.container.factory.IJp2pComponents;
import net.jp2p.container.factory.IPropertySourceFactory;
import net.jp2p.container.partial.PartialPropertySource;
import net.jp2p.container.properties.AbstractJp2pPropertySource;
import net.jp2p.container.properties.IJp2pDirectives;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IPropertyConvertor;
import net.jp2p.container.properties.ManagedProperty;
import net.jp2p.container.properties.IJp2pDirectives.Directives;
import net.jp2p.container.seeds.SeedInfo;
import net.jp2p.container.utils.StringDirective;
import net.jp2p.container.utils.StringStyler;
import net.jp2p.container.utils.Utils;
import net.jp2p.jxta.advertisement.AdvertisementPreferences;
import net.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.jp2p.jxta.discovery.DiscoveryPreferences;
import net.jp2p.jxta.discovery.DiscoveryPropertySource;
import net.jp2p.jxta.network.INetworkPreferences;
import net.jp2p.jxta.network.NetworkManagerPreferences;
import net.jp2p.jxta.network.NetworkManagerPropertySource;
import net.jp2p.jxta.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.jp2p.jxta.network.configurator.NetworkConfigurationFactory;
import net.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource;
import net.jp2p.jxta.network.configurator.OverviewPreferences;
import net.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.jp2p.jxta.pipe.PipePropertySource;
import net.jp2p.jxta.registration.RegistrationPropertySource;
import net.jp2p.jxta.seeds.SeedListPropertySource;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class Jp2pHandler extends DefaultHandler implements IContextEntities{

	public static final int MAX_COUNT = 200;	

	private ManagedProperty<IJp2pProperties,Object> property;

	private IContainerBuilder container;
	private ContextLoader contexts;
	private ContainerFactory root;
	private FactoryNode node;
	private String bundleId;
	private Class<?> clss;
	private Stack<String> stack;

	private static Logger logger = Logger.getLogger( XMLFactoryBuilder.class.getName() );

	public Jp2pHandler( IContainerBuilder container, ContextLoader contexts, String bundleId, Class<?> clss ) {
		this.bundleId = bundleId;
		this.container = container;
		this.contexts = contexts;
		this.clss = clss;
		this.stack = new Stack<String>();
	}

	/**
	 * Get the root factory 
	 * @return
	 */
	ContainerFactory getRoot() {
		return root;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, 
			Attributes attributes) throws SAXException {
		IPropertySourceFactory factory = null;
		//First check for groups
		if( Groups.isGroup( qName )){
			stack.push( qName );
			return;
		}
		//The name is not a group. try the default JP2P components
		if( Jp2pContext.Components.isComponent( qName )){
			IJp2pComponents current = Jp2pContext.Components.valueOf( StringStyler.styleToEnum( qName ));
			switch(( Jp2pContext.Components )current ){
			case JP2P_CONTAINER:
				factory = container.getFactory( Jp2pContext.Components.JP2P_CONTAINER.toString() );
				if( factory == null )
					factory = new ContainerFactory( container, bundleId );
				this.root = (ContainerFactory) factory;
				break;
			case CONTEXT:
				return;//skip, the contexts were parsed in the first round
			default:
				factory = this.getFactory( qName, attributes, node.getData().getPropertySource());
				break;
			}
		}
		//Apparently the factory is available elsewhere
		if( factory == null ){
			factory = this.getFactoryFromClass(qName, attributes, node.getData().getPropertySource());
			if( factory == null ){
				factory = this.getFactory( qName, attributes, node.getData().getPropertySource());
			}
		}
		if( factory != null ){
			node = this.processFactory(attributes, node, factory);
			this.stack.push( qName );
			return;
		}else{
			try{
				this.property = this.createProperty(qName, attributes );
			}
			catch( Exception ex ){
				logger.log( Level.SEVERE, "\n\n !!!Value " + qName + " incorrectly parsed as property.\n\n\n");
				ex.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected IPropertySourceFactory getFactory( String componentName, Attributes attributes, IJp2pPropertySource<?> parentSource ){
		String contextName = attributes.getValue(Directives.CONTEXT.toString().toLowerCase());
		if( Utils.isNull( contextName )){
			contextName = AbstractJp2pPropertySource.findFirstAncestorDirective( parentSource, Directives.CONTEXT );
		}
		IJp2pContext context = getContext( contexts, contextName );
		if( context == null )
			context = this.contexts.getContextForComponent( contextName, componentName);
		return context.getFactory( this.container, attributes, (IJp2pPropertySource<IJp2pProperties>) parentSource, componentName);
	}

	/**
	 * Create a factory from a class definition
	 * @param componentName
	 * @param attributes
	 * @param parentSource
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected IPropertySourceFactory getFactoryFromClass( String componentName, Attributes attributes, IJp2pPropertySource<?> parentSource ){
		if( attributes.getValue(Directives.CLASS.toString().toLowerCase()) == null )
			return null;
		String className = attributes.getValue(Directives.CLASS.toString().toLowerCase());
		Class<?> factoryClass = null;
		try{
			factoryClass = clss.getClassLoader().loadClass( className );
		}
		catch( ClassNotFoundException ex1 ){
			try{
				factoryClass = XMLFactoryBuilder.class.getClassLoader().loadClass( className );
			}
			catch( ClassNotFoundException ex2 ){ /* do nothing */ }
		}
		if( factoryClass != null  ){
			try {
				Constructor<IComponentFactory<?>> constructor = (Constructor<IComponentFactory<?>>) factoryClass.getDeclaredConstructor(IJp2pPropertySource.class );
				return constructor.newInstance( node.getData().getPropertySource() );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Process the factory by adding the directives and adding it to the container
	 * @param attributes
	 * @param parent
	 * @param factory
	 * @return
	 */
	protected FactoryNode processFactory( Attributes attributes, FactoryNode parent, IPropertySourceFactory factory ){
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
		container.addFactory( factory );
		if( node == null )
			return new FactoryNode( factory );
		else
			return (FactoryNode) node.addChild(factory);	
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
		if( !stack.peek().equals( qName ))
			return;
		if(!Groups.isGroup(qName ))
			node = (FactoryNode) node.getParent();
		this.property = null;
		this.stack.pop();
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
		IJp2pProperties id = property.getKey();
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) node.getData().getPropertySource();
		String contextName = source.getDirective( Directives.CONTEXT );
		IJp2pContext context = contexts.getContext(contextName);
		if( source instanceof Jp2pContainerPropertySource ){
			Jp2pContainerPreferences preferences = new Jp2pContainerPreferences( (Jp2pContainerPropertySource) source );
			preferences.setPropertyFromConverion( property.getKey(), value);
			return;
		}
		if( source instanceof NetworkManagerPropertySource ){
			NetworkManagerPreferences preferences = new NetworkManagerPreferences( source );
			preferences.setPropertyFromConverion( (NetworkManagerProperties) property.getKey(), value);
			return;
		}
		if( source instanceof NetworkConfigurationPropertySource ){
			OverviewPreferences preferences = new OverviewPreferences( source );
			preferences.setPropertyFromConverion( (NetworkConfiguratorProperties) property.getKey(), value);
			return;
		}
		if( source instanceof DiscoveryPropertySource ){
			DiscoveryPreferences preferences = new DiscoveryPreferences( source );
			preferences.setPropertyFromConverion( id, value);
			return;
		}
		if( source instanceof PipePropertySource ){
			PipePropertySource pps = ( PipePropertySource )source ;
			pps.setProperty( id, value);
			return;
		}
		if( source instanceof PeerGroupPropertySource ){
			IPropertyConvertor<String, Object> convertor = context.getConvertor(source);
			property.setValue( convertor.convertTo(property.getKey(), value));
			return;
		}
		if( source instanceof RegistrationPropertySource ){
			//RegistrationPreferences preferences = new RegistrationPreferences( source );
			//preferences.setPropertyFromString(( RegistrationProperties) property.getKey(), value);
			return;
		}
		
		if( source instanceof AdvertisementPropertySource ){
			AdvertisementPreferences preferences = new AdvertisementPreferences( source );
			preferences.setPropertyFromConverion( id, value);
			return;
		}
		if( source instanceof PartialPropertySource ){
			INetworkPreferences preferences = NetworkConfigurationFactory.getPreferences((PartialPropertySource) source);
			if( preferences != null )
				preferences.convertTo( (NetworkConfiguratorProperties) property.getKey(), value);
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


	@Override
	public void error(SAXParseException e) throws SAXException {
		print(e);
		super.error(e);
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

	/**
	 * Get the context, or try to load it if none was found
	 * @param source
	 * @return
	 */
	private static IJp2pContext getContext( ContextLoader contexts, String contextName ){
		if( Utils.isNull( contextName ))
			return null;
		return contexts.getContext(contextName);
	}

	/**
	 * The factory node is used to create a treemap of the property sources 
	 * @author Kees
	 *
	 */
	private class FactoryNode extends ComponentNode<IPropertySourceFactory>{

		protected FactoryNode(IPropertySourceFactory data, FactoryNode parent) {
			super((IPropertySourceFactory) data, parent);
		}

		public FactoryNode(IPropertySourceFactory data) {
			super((IPropertySourceFactory) data);
		}

		@Override
		public ComponentNode<?> addChild(Object data) {
			FactoryNode child = new FactoryNode( (IPropertySourceFactory) data, this );
			super.getChildrenAsCollection().add(child);
			return child;
		}	
	}
}