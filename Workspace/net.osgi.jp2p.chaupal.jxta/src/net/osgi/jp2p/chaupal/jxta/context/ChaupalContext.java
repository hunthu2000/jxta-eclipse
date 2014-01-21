package net.osgi.jp2p.chaupal.jxta.context;

import org.xml.sax.Attributes;

import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.chaupal.jxta.IChaupalComponents.ChaupalComponents;
import net.osgi.jp2p.chaupal.jxta.advertisement.ChaupalAdvertisementFactory;
import net.osgi.jp2p.chaupal.jxta.discovery.ChaupalDiscoveryServiceFactory;
import net.osgi.jp2p.chaupal.jxta.peergroup.ChaupalPeerGroupFactory;
import net.osgi.jp2p.chaupal.jxta.pipe.ChaupalPipeFactory;
import net.osgi.jp2p.chaupal.persistence.PersistedProperty;
import net.osgi.jp2p.context.IJp2pContext;
import net.osgi.jp2p.context.Jp2pContext;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.jxta.discovery.DiscoveryServiceFactory;
import net.osgi.jp2p.jxta.netpeergroup.NetPeerGroupFactory;
import net.osgi.jp2p.jxta.peergroup.PeerGroupFactory;
import net.osgi.jp2p.jxta.pipe.PipeServiceFactory;
import net.osgi.jp2p.jxta.registration.RegistrationServiceFactory;
import net.osgi.jp2p.persistence.IPersistedProperty;
import net.osgi.jp2p.persistence.PersistenceFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Contexts;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class ChaupalContext implements IJp2pContext<Object> {

	@Override
	public String getName() {
		return Contexts.CHAUPAL.toString();
	}

	/**
	 * Returns true if the given component name is valid for this context
	 * @param componentName
	 * @return
	 */
	@Override
	public boolean isValidComponentName( String contextName, String componentName ){
		if( !Utils.isNull( contextName ) && !Jp2pContext.isContextNameEqual(Contexts.CHAUPAL.toString(), contextName ))
			return false;
		if( ChaupalComponents.isComponent( componentName ))
			return true;
		JxtaContext jc = new JxtaContext();
		return jc.isValidComponentName( Contexts.JXTA.toString(), componentName);
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
	public IComponentFactory<?> getFactory( IContainerBuilder builder, Attributes attributes, IJp2pPropertySource<IJp2pProperties> parentSource, String componentName ){
		IComponentFactory<?> factory = null;
		String str = StringStyler.styleToEnum( componentName );
		if(ChaupalComponents.isComponent(str)){
			ChaupalComponents comp = ChaupalComponents.valueOf(str );
			switch( comp ){
			case ADVERTISEMENT_SERVICE:
				//AdvertisementTypes adType = 
				//AdvertisementTypes.convertFrom( attributes.getValue( AdvertisementDirectives.TYPE.toString().toLowerCase() ));
				//TODO removefactory = new ChaupalAdvertisementFactory<Advertisement>( builder, adType, parentSource );
				break;
			case DISCOVERY_SERVICE:
				factory = new ChaupalDiscoveryServiceFactory( builder, parentSource );
				break;
			case PIPE_SERVICE:
				factory = new ChaupalPipeFactory( builder, parentSource );
				break;
			case PEERGROUP_SERVICE:
				factory = new ChaupalPeerGroupFactory( builder, parentSource );
				break;
			default:
				break;
			}
		}
		if( factory != null )
			return factory;
		JxtaContext jc = new JxtaContext();
		if( jc.isValidComponentName( Contexts.JXTA.toString(), componentName))
			factory = jc.getFactory(builder, attributes, parentSource, componentName);
		return factory;

	}

	/**
	 * Get the default factory for this container
	 * @param parent
	 * @param componentName
	 * @return
	 */
	public static IComponentFactory<?> getDefaultFactory( IContainerBuilder builder, String[] attributes, IJp2pPropertySource<IJp2pProperties> parentSource, String componentName ){
		if( Utils.isNull(componentName))
			return null;
		String comp = StringStyler.styleToEnum(componentName);
		if( !ChaupalComponents.isComponent( comp ))
			return null;
		ChaupalComponents component = ChaupalComponents.valueOf(comp);
		IComponentFactory<?> factory = null;
		switch( component ){
		case PERSISTENCE_SERVICE:
			IPersistedProperty<?> property = new PersistedProperty<Object>( null );
			factory = new PersistenceFactory( builder, parentSource, property );
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
		case ADVERTISEMENT_SERVICE:
			//factory = new Jp2pAdvertisementFactory<Advertisement>( builder, parentSource );
			break;
		default:
			break;
		}
		return factory;
	}

}
