package net.osgi.jxse.context;

import net.osgi.jxse.component.AbstractJxseModule;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;

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
	public IComponentFactory<JxseServiceContext, IJxseProperties> createFactory( IPeerGroupProvider provider ) {
		return new ContextFactory( super.getPropertySource() );
	}
}
