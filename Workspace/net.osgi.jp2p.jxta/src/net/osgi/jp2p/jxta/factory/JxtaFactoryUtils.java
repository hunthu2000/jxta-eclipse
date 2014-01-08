package net.osgi.jp2p.jxta.factory;

import net.jxta.document.Advertisement;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.jxta.advertisement.Jp2pAdvertisementFactory;
import net.osgi.jp2p.jxta.advertisement.JxtaAdvertisementFactory;
import net.osgi.jp2p.jxta.discovery.DiscoveryServiceFactory;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.jxta.netpeergroup.NetPeerGroupFactory;
import net.osgi.jp2p.jxta.network.NetworkManagerFactory;
import net.osgi.jp2p.jxta.network.configurator.NetworkConfigurationFactory;
import net.osgi.jp2p.jxta.peergroup.PeerGroupFactory;
import net.osgi.jp2p.jxta.pipe.PipeServiceFactory;
import net.osgi.jp2p.jxta.registration.RegistrationServiceFactory;
import net.osgi.jp2p.jxta.seeds.SeedListFactory;
import net.osgi.jp2p.partial.PartialFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class JxtaFactoryUtils {

	/**
	 * Get the default factory for this container
	 * @param parent
	 * @param componentName
	 * @return
	 */
	public static IComponentFactory<?> getDefaultFactory( ContainerBuilder builder, IJp2pPropertySource<IJp2pProperties> parentSource, String componentName ){
		if( Utils.isNull(componentName))
			return null;
		String comp = StringStyler.styleToEnum(componentName);
		if( !JxtaComponents.isComponent( comp ))
			return null;
		JxtaComponents component = JxtaComponents.valueOf(comp);
		IComponentFactory<?> factory = null;
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
			factory = new PartialFactory<Object>( builder, componentName, parentSource );
			break;
		case NET_PEERGROUP_SERVICE:
			factory = new NetPeerGroupFactory( builder, parentSource );
			break;			
		case PIPE_SERVICE:
			factory = new PipeServiceFactory( builder, parentSource );
			break;			
		case REGISTRATION_SERVICE:
			factory = new RegistrationServiceFactory( builder, parentSource );
			break;
		case DISCOVERY_SERVICE:
			factory = new DiscoveryServiceFactory( builder, parentSource );
			break;			
		case PEERGROUP_SERVICE:
			factory = new PeerGroupFactory( builder, parentSource );
			break;			
		case ADVERTISEMENT:
			factory = new JxtaAdvertisementFactory( builder, parentSource );
			break;
		case ADVERTISEMENT_SERVICE:
			factory = new Jp2pAdvertisementFactory<Advertisement>( builder, parentSource );
			break;
		default:
			break;
		}
		return factory;
	}

}
