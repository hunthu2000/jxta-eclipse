package net.osgi.jxse.partial;

import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class PartialModule<T extends Object> extends AbstractJxseModule<T, PartialPropertySource> {

	private String componentName;
	private boolean completed;
	
	public PartialModule( String componentName, IJxseModule<?> parent) {
		super(parent);
		this.componentName = componentName;
		this.completed = false;
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
		this.completed = true;
		return null;
	}
	
	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
