package net.osgi.jxse.registration;

import net.osgi.jxse.component.AbstractJxseModule;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class RegistrationModule extends AbstractJxseModule<RegistrationService, RegistrationPropertySource> {

	public RegistrationModule() {
		super();
	}

	public RegistrationModule(IJxsePropertySource<?> parent) {
		super(parent);
	}

	@Override
	public String getComponentName() {
		return Components.PIPE_SERVICE.toString();
	}


	@Override
	protected RegistrationPropertySource onCreatePropertySource() {
		return new RegistrationPropertySource( super.getParent() );
	}

	@Override
	public IComponentFactory<RegistrationService, IJxseProperties> createFactory( IPeerGroupProvider provider ) {
		return new RegistrationServiceFactory( provider, super.getPropertySource() );
	}
}
