package net.osgi.jxse.registration;

import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;

public class RegistrationModule extends AbstractJxseModule<RegistrationService, RegistrationPropertySource> {

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
		return new RegistrationServiceFactory( super.getPropertySource() );
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
