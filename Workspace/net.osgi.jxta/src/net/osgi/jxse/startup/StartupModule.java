package net.osgi.jxse.startup;

import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.builder.container.BuilderContainer;
import net.osgi.jxse.context.AbstractServiceContext;
import net.osgi.jxse.context.ContextModule;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.context.JxseServiceContext;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.netpeergroup.NetPeerGroupModule;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class StartupModule extends AbstractJxseModule<JxseStartupService, JxseStartupPropertySource> {

	private BuilderContainer container;
	
	public StartupModule( BuilderContainer container, ContextModule parent ) {
		super(parent, 1);
		this.container = container;
	}

	@Override
	public String getComponentName() {
		return Components.STARTUP_SERVICE.toString();
	}

	@Override
	protected JxseStartupPropertySource onCreatePropertySource() {
		JxseStartupPropertySource source = new JxseStartupPropertySource( (JxseContextPropertySource) super.getParent().getPropertySource());
		return source;
	}

	/**
	 * Extend the property sources with a net peergroup if this is required
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void extendModules() {
		super.extendModules();
		JxseStartupPropertySource.setParentDirective(Directives.AUTO_START, super.getPropertySource());
		IJxseModule<?> module = container.getModule( Components.NET_PEERGROUP_SERVICE.toString() );
		IJxseWritePropertySource<?> nmm = (IJxseWritePropertySource<?>) JxseStartupPropertySource.findPropertySource(this.getParent().getPropertySource(), Components.NETWORK_MANAGER.toString());
		nmm.setDirective( Directives.AUTO_START, this.getPropertySource().getDirective( Directives.AUTO_START));
		if( module == null ){
			module = new NetPeerGroupModule( super.getParent() );
			container.addModule((IJxseModule<Object>) module);
			super.getParent().getPropertySource().addChild(module.createPropertySource());
		}
	}

	@Override
	public IComponentFactory<JxseStartupService> onCreateFactory() {
		StartupServiceFactory factory = new StartupServiceFactory( container, super.getPropertySource() );
		JxseStartupService service = factory.createComponent();
		JxseServiceContext.addModule( (AbstractServiceContext) super.getParent().createFactory().createComponent(), service );
		service.start();
		factory.complete();
		return factory;
	}
}
