package net.osgi.jxse.pipe;

import net.jxta.pipe.PipeService;
import net.osgi.jxse.component.AbstractJxseModule;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class PipeModule extends AbstractJxseModule<PipeService, PipePropertySource> {

	public PipeModule(IJxsePropertySource<?> parent) {
		super(parent);
	}

	@Override
	public String getComponentName() {
		return Components.PIPE_SERVICE.toString();
	}


	@Override
	protected PipePropertySource onCreatePropertySource() {
		return new PipePropertySource( super.getParent() );
	}

	@Override
	public IComponentFactory<PipeService, IJxseProperties> createFactory( IPeerGroupProvider provider ) {
		return new PipeServiceFactory( provider, super.getPropertySource() );
	}
}
