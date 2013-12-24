package net.osgi.jxse.pipe;

import net.jxta.pipe.PipeService;
import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;

public class PipeModule extends AbstractJxseModule<PipeService, PipePropertySource> {

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
		return new PipeServiceFactory( super.getPropertySource() );
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
