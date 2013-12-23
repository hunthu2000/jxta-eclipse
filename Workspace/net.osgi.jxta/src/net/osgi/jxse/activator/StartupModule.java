package net.osgi.jxse.activator;

import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.builder.container.BuilderContainer;
import net.osgi.jxse.context.ContextModule;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.netpeergroup.NetPeerGroupModule;
import net.osgi.jxse.properties.IJxseDirectives.Directives;

public class StartupModule extends AbstractJxseModule<JxseStartupService, JxseStartupPropertySource> {

	private ContextModule parent;
	private BuilderContainer container;
	
	public StartupModule( BuilderContainer container, ContextModule parent ) {
		super(parent, 1);
		this.container = container;
		this.parent = parent;
	}

	@Override
	public String getComponentName() {
		return Components.STARTUP_SERVICE.toString();
	}

	@Override
	protected JxseStartupPropertySource onCreatePropertySource() {
		JxseStartupPropertySource source = new JxseStartupPropertySource( (JxseContextPropertySource) super.getParent().getPropertySource());
		JxseStartupPropertySource.setParentDirective(Directives.AUTO_START, source);
		return source;
	}

	/**
	 * Extend the property sources with a net peergroup if this is required
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void extendModules() {
		super.extendModules();
		IJxseModule<?> module = container.getModule( Components.NET_PEERGROUP_SERVICE.toString() );
		if( module == null ){
			module = new NetPeerGroupModule( parent );
			container.addModule((IJxseModule<Object>) module);
			parent.getPropertySource().addChild(module.createPropertySource());
		}
	}

	@Override
	public IComponentFactory<JxseStartupService> onCreateFactory() {
		StartupServiceFactory factory = new StartupServiceFactory( container, super.getPropertySource() );
		JxseStartupService service = factory.createComponent();
		service.start();
		return factory;
	}

	@Override
	public void notifyCreated(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
