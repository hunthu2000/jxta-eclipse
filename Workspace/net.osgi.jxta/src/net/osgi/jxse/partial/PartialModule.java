package net.osgi.jxse.partial;

import net.osgi.jxse.component.AbstractJxseModule;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class PartialModule<T extends Object> extends AbstractJxseModule<T, PartialPropertySource> {

	private String componentName;
	
	public PartialModule( String componentName, IJxsePropertySource<?> parent) {
		super(parent);
		this.componentName = componentName;
	}

	@Override
	public String getComponentName() {
		return componentName;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected PartialPropertySource onCreatePropertySource() {
		return new PartialPropertySource( this.componentName, (IJxsePropertySource<IJxseProperties>) super.getParent() );
	}

	@Override
	public IComponentFactory<T, IJxseProperties> createFactory( IPeerGroupProvider provider ) {
		return null;
	}
}
