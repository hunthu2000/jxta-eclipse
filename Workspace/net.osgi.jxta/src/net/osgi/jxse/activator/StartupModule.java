package net.osgi.jxse.activator;

import net.osgi.jxse.component.AbstractJxseModule;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class StartupModule extends AbstractJxseModule<JxseStartupService, JxseStartupPropertySource> {

	public StartupModule(IJxsePropertySource<?> parent) {
		super(parent);
	}

	@Override
	public String getComponentName() {
		return Components.STARTUP_SERVICE.toString();
	}

	@Override
	protected JxseStartupPropertySource onCreatePropertySource() {
		return new JxseStartupPropertySource( (JxseContextPropertySource) super.getParent());
	}

	@Override
	public IComponentFactory<JxseStartupService, IJxseProperties> createFactory( IPeerGroupProvider provider ) {
		return new StartupServiceFactory( super.getPropertySource() );
	}
}
