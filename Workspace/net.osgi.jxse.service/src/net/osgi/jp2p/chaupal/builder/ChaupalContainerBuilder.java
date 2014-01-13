package net.osgi.jp2p.chaupal.builder;

import net.jxta.document.Advertisement;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.chaupal.advertisement.ChaupalAdvertisementFactory;
import net.osgi.jp2p.chaupal.discovery.ChaupalDiscoveryServiceFactory;
import net.osgi.jp2p.chaupal.pipe.ChaupalPipeFactory;
import net.osgi.jp2p.container.Jp2pContainerPropertySource;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.jxta.discovery.DiscoveryServiceFactory;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory;
import net.osgi.jp2p.jxta.pipe.PipeServiceFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Contexts;
import net.osgi.jp2p.utils.StringStyler;

public class ChaupalContainerBuilder extends ContainerBuilder {

	@Override
	public boolean addFactory(IComponentFactory<?> factory) {
		return super.addFactory( getChaupalFactory( factory));
	}

	@Override
	public IComponentFactory<?> addFactoryToContainer(String componentName,
			IJp2pPropertySource<IJp2pProperties> parentSource,
			boolean createSource, boolean blockCreation) {
		return getChaupalFactory( super.addFactoryToContainer(componentName, parentSource, createSource,
				blockCreation));
	}

	/**
	 * Change the factory to a Chaupal factory if required and available
	 * @param factory
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected IComponentFactory<?> getChaupalFactory( IComponentFactory<?> base ){
		if( isChaupalFactory(base))
			return base;
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
				factory = new ChaupalAdvertisementFactory<Advertisement>(this, (IComponentFactory<Advertisement>)base);
				factory.createPropertySource();
				break;
			case DISCOVERY_SERVICE:
				factory = new ChaupalDiscoveryServiceFactory( this, (DiscoveryServiceFactory)base );
				factory.createPropertySource();
				break;
			case PIPE_SERVICE:
				factory = new ChaupalPipeFactory(this, (PipeServiceFactory) base);
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
}
