package net.jp2p.chaupal.jxta.context;

import java.net.URL;

import org.xml.sax.Attributes;

import net.jp2p.chaupal.jxta.advertisement.ChaupalAdvertisementFactory;
import net.jp2p.chaupal.jxta.discovery.ChaupalDiscoveryServiceFactory;
import net.jp2p.chaupal.jxta.pipe.ChaupalPipeFactory;
import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.context.IJp2pContext;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.factory.IComponentFactory;
import net.jp2p.container.factory.IPropertySourceFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IPropertyConvertor;
import net.jp2p.container.properties.IJp2pDirectives.Contexts;
import net.jp2p.container.utils.StringStyler;
import net.jp2p.container.utils.Utils;
import net.jp2p.container.xml.IJp2pHandler;
import net.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.jp2p.jxta.discovery.DiscoveryPreferences;
import net.jp2p.jxta.factory.JxtaFactoryUtils;
import net.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.jp2p.jxta.network.NetworkManagerPreferences;
import net.jp2p.jxta.peergroup.PeerGroupPreferences;
import net.jxta.document.Element;
import net.jxta.impl.loader.RefJxtaLoader;
import net.jxta.impl.peergroup.CompatibilityEquater;
import net.jxta.impl.peergroup.CompatibilityUtils;
import net.jxta.impl.peergroup.MultiLoader;
import net.jxta.platform.IJxtaLoader;

public class JxtaContext implements IJp2pContext {

    /**
     * Default compatibility equater instance.
     */
    private static final CompatibilityEquater COMP_EQ =
    	new CompatibilityEquater() {
        @SuppressWarnings("rawtypes")
		public boolean compatible(Element test) {
            return CompatibilityUtils.isCompatible(test);
        }
    };

	private static MultiLoader loader = MultiLoader.getInstance();
	
	public JxtaContext() {
		IJxtaLoader jxtaLoader = new RefJxtaLoader( new URL[0], COMP_EQ );
		loader.addLoader(jxtaLoader);
	}

	@Override
	public String getName() {
		return Contexts.JXTA.toString();
	}

	/**
	 * Get the supported services
	 */
	@Override
	public String[] getSupportedServices() {
		JxtaComponents[] components = JxtaComponents.values();
		String[] names = new String[ components.length ];
		for( int i=0; i<components.length; i++ )
			names[i] = components[i].toString();
		return names;
	}

	/**
	 * Returns true if the given component name is valid for this context
	 * @param componentName
	 * @return
	 */
	public boolean isValidComponentName( String contextName, String componentName ){
		if( !Utils.isNull( contextName ) && !Jp2pContext.isContextNameEqual(Contexts.JXTA.toString(), contextName ))
			return false;
		return JxtaComponents.isComponent( componentName );
	}

	/**
	 * Returns true if the given factory is a chaupal factory
	 * @param factory
	 * @return
	 */
	public boolean isChaupalFactory( IComponentFactory<?> factory ){
		if( factory instanceof ChaupalAdvertisementFactory )
			return true;
		if( factory instanceof ChaupalDiscoveryServiceFactory )
			return true;
		return ( factory instanceof ChaupalPipeFactory );
	}

	/**
	 * Change the factory to a Chaupal factory if required and available
	 * @param factory
	 * @return
	 */
	@Override
	public IPropertySourceFactory<?> getFactory( IContainerBuilder builder, Attributes attributes, IJp2pPropertySource<IJp2pProperties> parentSource, String componentName ){
		JxtaComponents component = JxtaComponents.valueOf( StringStyler.styleToEnum(componentName));
		String[] attrs;
		switch( component ){
		case ADVERTISEMENT:
			String adType = attributes.getValue( AdvertisementDirectives.TYPE.toString().toLowerCase() );
			attrs = new String[1];
			attrs[0] = adType;
			break;
		default:
			attrs = new String[0];
		}
		IPropertySourceFactory<?> factory = JxtaFactoryUtils.getDefaultFactory(builder, attrs, parentSource, componentName);
		return factory;
	}

	@Override
	public Object createValue( String componentName, IJp2pProperties id ){
		return null;
	}
	
	/**
	 * Get the default factory for this container
	 * @param parent
	 * @param componentName
	 * @return
	 */
	public IPropertyConvertor<String, Object> getConvertor( IJp2pWritePropertySource<IJp2pProperties> source ){
		String comp = StringStyler.styleToEnum( source.getComponentName());
		if( !JxtaComponents.isComponent( comp ))
			return null;
		JxtaComponents component = JxtaComponents.valueOf(comp);
		IPropertyConvertor<String, Object> convertor = null;
		switch( component ){
		case NETWORK_MANAGER:
			convertor = new NetworkManagerPreferences( source );
			break;			
		case NET_PEERGROUP_SERVICE:
			convertor = new NetworkManagerPreferences( source );
			break;			
		case DISCOVERY_SERVICE:
			convertor = new DiscoveryPreferences( source );
			break;			
		case PEERGROUP_SERVICE:
			convertor = new PeerGroupPreferences( source );
			break;			
		default:
			break;
		}
		return convertor;
	}

	@Override
	public IJp2pHandler getHandler() {
		// TODO Auto-generated method stub
		return null;
	}

}
