package net.osgi.jxse.pipe;

import net.jxta.pipe.PipeService;
import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider;

public class PipeModule extends AbstractJxseModule<PipeService, PipePropertySource> {

	private  IPeerGroupProvider provider;

	public PipeModule(IJxseModule<?> parent) {
		super(parent);
	}

	@Override
	public String getComponentName() {
		return Components.PIPE_SERVICE.toString();
	}


	@Override
	protected PipePropertySource onCreatePropertySource() {
		return new PipePropertySource( super.getParent().getPropertySource() );
	}

	@Override
	public IComponentFactory<PipeService> onCreateFactory() {
		return new PipeServiceFactory( provider, super.getPropertySource() );
	}

	@Override
	public void notifyCreated(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
