package net.jp2p.network.jxta.utils;

import net.jp2p.chaupal.jxta.network.NetworkManagerFactory;
import net.jp2p.chaupal.jxta.network.configurator.NetworkConfigurationFactory;
import net.jp2p.chaupal.jxta.network.configurator.partial.PartialNetworkConfigFactory;
import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.factory.IPropertySourceFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.utils.StringStyler;
import net.jp2p.container.utils.Utils;
import net.jp2p.jxta.factory.IJxtaComponents.JxtaNetworkComponents;
import net.jp2p.jxta.seeds.SeedListFactory;

public class JxtaFactoryUtils {

	/**
	 * Get the default factory for this container
	 * @param parent
	 * @param componentName
	 * @return
	 */
	public static IPropertySourceFactory getDefaultFactory( IContainerBuilder builder, String[] attributes, IJp2pPropertySource<IJp2pProperties> parentSource, String componentName ){
		if( Utils.isNull(componentName))
			return null;
		String comp = StringStyler.styleToEnum(componentName);
		if( !JxtaNetworkComponents.isComponent( comp ))
			return null;
		JxtaNetworkComponents component = JxtaNetworkComponents.valueOf(comp);
		IPropertySourceFactory factory = null;
		switch( component ){
		case NETWORK_MANAGER:
			factory = new NetworkManagerFactory( builder, parentSource  );
			break;
		case NETWORK_CONFIGURATOR:
			factory = new NetworkConfigurationFactory( builder, parentSource );
			break;
		case SEED_LIST:
			factory = new SeedListFactory( builder, parentSource );
			break;
		case TCP:
		case HTTP:
		case HTTP2:
		case MULTICAST:
		case SECURITY:
			factory = new PartialNetworkConfigFactory<Object>( builder, componentName, parentSource );
			break;
		default:
			break;
		}
		return factory;
	}

	/**
	 * Get or create a corresponding factory for a child component of the given source, with the given component name.
	 * @param source: the source who should have a child source
	 * @param componentName: the required component name of the child
	 * @param createSource: create the property source immediately
	 * @return
	 */
	public static IPropertySourceFactory getOrCreateChildFactory( IContainerBuilder builder, String[] attributes, IJp2pPropertySource<IJp2pProperties> source, String componentName, boolean createSource ){
		IJp2pPropertySource<?> child = source.getChild( componentName ); 
		if( child != null )
			return builder.getFactory(child );
		IPropertySourceFactory factory = getDefaultFactory(builder, attributes, source, componentName );
		if( createSource )
			factory.createPropertySource();
		builder.addFactory( factory );
		return factory;
	}

}
