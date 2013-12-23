package net.osgi.jxse.context;

import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;

public class ContextModule extends AbstractJxseModule<JxseServiceContext, JxseContextPropertySource> {

	private String bundleId;
	private String identifier;
	
	public ContextModule( String bundleId, String identifier ) {
		super();
		this.bundleId = bundleId;
		this.identifier = identifier;
	}

	public ContextModule( JxseContextPropertySource source ) {
		super();
		super.setSource( source );
	}

	@Override
	public String getComponentName() {
		return Components.JXSE_CONTEXT.toString();
	}

	@Override
	protected JxseContextPropertySource onCreatePropertySource() {
		return new JxseContextPropertySource( this.bundleId, this.identifier );
	}

	@Override
	public IComponentFactory<JxseServiceContext> onCreateFactory() {
		return new ContextFactory( super.getPropertySource() );
	}

	@Override
	public void notifyCreated(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
