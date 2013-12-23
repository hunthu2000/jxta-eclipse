package net.osgi.jxse.discovery;

import net.jxta.discovery.DiscoveryService;
import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider;

public class DiscoveryModule extends AbstractJxseModule<DiscoveryService, DiscoveryPropertySource> {

	private IPeerGroupProvider provider;
	public DiscoveryModule() {
		super();
	}

	public DiscoveryModule(IJxseModule<?> parent) {
		super(parent);
	}

	@Override
	public String getComponentName() {
		return Components.PIPE_SERVICE.toString();
	}


	@Override
	protected DiscoveryPropertySource onCreatePropertySource() {
		return new DiscoveryPropertySource( super.getParent().getPropertySource() );
	}

	@Override
	public IComponentFactory<DiscoveryService> onCreateFactory() {
		return new DiscoveryServiceFactory( provider, super.getPropertySource() );
	}

	@Override
	public void notifyCreated(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
