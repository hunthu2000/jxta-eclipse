package net.osgi.jxse.context;

import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.IJxsePropertySource;

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
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		switch( event.getBuilderEvent()){
		case PROPERTY_SOURCE_CREATED:
			IJxsePropertySource<?> sups = 
			JxseContextPropertySource.findPropertySource(super.getPropertySource(), Components.STARTUP_SERVICE.toString());
			//if( sups != null )
				break;
			//super.getPropertySource().addChild( new StartupPropertySource)
		case COMPONENT_CREATED:
			IJxseModule<?>parent = event.getModule().getParent();
			if(( parent == null ) || (!parent.equals( this )))
				return;
			JxseServiceContext.addModule( this.getComponent(), event.getModule().getComponent());
			break;
		default:
			break;
		}
	}
}
