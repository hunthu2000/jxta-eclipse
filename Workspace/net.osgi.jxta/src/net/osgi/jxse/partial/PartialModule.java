package net.osgi.jxse.partial;

import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class PartialModule<T extends Object> extends AbstractJxseModule<T, PartialPropertySource> {

	private String componentName;
	
	public PartialModule( String componentName, IJxseModule<?> parent) {
		super(parent);
		this.componentName = componentName;
	}

	@Override
	public String getComponentName() {
		return componentName;
	}

	@Override
	protected PartialPropertySource onCreatePropertySource() {
		return new PartialPropertySource( this.componentName, (IJxsePropertySource<IJxseProperties>) super.getParent().getPropertySource() );
	}

	@Override
	public IComponentFactory<T> onCreateFactory() {
		return null;
	}

	@Override
	public void notifyCreated(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
