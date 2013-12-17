package net.osgi.jxse.discovery;

import net.jxta.discovery.DiscoveryService;
import net.osgi.jxse.component.AbstractJxseModule;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class DiscoveryModule extends AbstractJxseModule<DiscoveryService, DiscoveryPropertySource> {

	public DiscoveryModule() {
		super();
	}

	public DiscoveryModule(IJxsePropertySource<?> parent) {
		super(parent);
	}

	@Override
	public String getComponentName() {
		return Components.PIPE_SERVICE.toString();
	}


	@Override
	protected DiscoveryPropertySource onCreatePropertySource() {
		return new DiscoveryPropertySource( super.getParent() );
	}

	@Override
	public IComponentFactory<DiscoveryService, IJxseProperties> createFactory( IPeerGroupProvider provider ) {
		return new DiscoveryServiceFactory( provider, super.getPropertySource() );
	}
}
