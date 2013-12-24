package net.osgi.jxse.discovery;

import net.jxta.discovery.DiscoveryService;
import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;

public class DiscoveryModule extends AbstractJxseModule<DiscoveryService, DiscoveryPropertySource> {

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
		return new DiscoveryServiceFactory( super.getPropertySource() );
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
