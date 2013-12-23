package net.osgi.jxse.registration;

import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider;

public class RegistrationModule extends AbstractJxseModule<RegistrationService, RegistrationPropertySource> {

	private  IPeerGroupProvider provider;

	public RegistrationModule() {
		super();
	}

	public RegistrationModule(IJxseModule<?> parent) {
		super(parent);
	}

	@Override
	public String getComponentName() {
		return Components.PIPE_SERVICE.toString();
	}


	@Override
	protected RegistrationPropertySource onCreatePropertySource() {
		return new RegistrationPropertySource( super.getParent().getPropertySource() );
	}

	@Override
	public IComponentFactory<RegistrationService> onCreateFactory() {
		return new RegistrationServiceFactory( provider, super.getPropertySource() );
	}

	@Override
	public void notifyCreated(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
