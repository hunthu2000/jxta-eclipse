package net.osgi.jp2p.chaupal.jxta.context;

import org.xml.sax.Attributes;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.context.IJp2pContext;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.factory.IComponentFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pDirectives.Contexts;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IPropertyConvertor;
import net.jp2p.container.utils.StringStyler;
import net.jp2p.container.utils.Utils;
import net.osgi.jp2p.chaupal.jxta.IChaupalComponents.ChaupalComponents;
import net.osgi.jp2p.chaupal.jxta.advertisement.ChaupalAdvertisementFactory;
import net.osgi.jp2p.chaupal.jxta.discovery.ChaupalDiscoveryServiceFactory;
import net.osgi.jp2p.chaupal.jxta.peergroup.ChaupalPeerGroupFactory;
import net.osgi.jp2p.chaupal.jxta.persistence.OsgiPersistenceFactory;
import net.osgi.jp2p.chaupal.jxta.pipe.ChaupalPipeFactory;
import net.jp2p.jxta.discovery.DiscoveryPreferences;
import net.jp2p.jxta.discovery.DiscoveryServiceFactory;
import net.jp2p.jxta.netpeergroup.NetPeerGroupFactory;
import net.jp2p.jxta.network.NetworkManagerPreferences;
import net.jp2p.jxta.peergroup.PeerGroupFactory;
import net.jp2p.jxta.peergroup.PeerGroupPreferences;
import net.jp2p.jxta.pipe.PipeServiceFactory;
import net.jp2p.jxta.registration.RegistrationServiceFactory;

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
				factory = (IComponentFactory<?>) new ChaupalDiscoveryServiceFactory( builder, parentSource );
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
			factory = new OsgiPersistenceFactory( builder, parentSource );
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
		if( !ChaupalComponents.isComponent( comp ))
			return new JxtaContext().getConvertor(source);
		ChaupalComponents component = ChaupalComponents.valueOf(comp);
		IPropertyConvertor<String, Object> convertor = null;
		switch( component ){
		case NET_PEERGROUP_SERVICE:
			convertor = new NetworkManagerPreferences( source );
			break;			
		case DISCOVERY_SERVICE:
			convertor = new DiscoveryPreferences( source );
			break;			
		case PEERGROUP_SERVICE:
			convertor = new PeerGroupPreferences( source );
			break;			
		case ADVERTISEMENT_SERVICE:
			//factory = new Jp2pAdvertisementFactory<Advertisement>( builder, parentSource );
			break;
		default:
			break;
		}
		return convertor;
	}
}
