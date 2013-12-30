package net.osgi.jxse.partial;

import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class PartialFactory<T extends Object> extends AbstractComponentFactory<T> {

	private String componentName;
	private boolean completed;
	
	public PartialFactory( BuilderContainer container, String componentName, IJxsePropertySource<IJxseProperties> parentSource ){
		super( container, parentSource, false);
		this.componentName = componentName;
		this.completed = false;
	}

	@Override
	public String getComponentName() {
		return componentName;
	}

	@Override
	protected PartialPropertySource onCreatePropertySource() {
		return new PartialPropertySource( this.componentName, (IJxsePropertySource<IJxseProperties>) super.getParentSource() );
	}

	
	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IJxseComponent<T> onCreateComponent(IJxsePropertySource<IJxseProperties> properties) {
		// TODO Auto-generated method stub
		return null;
	}
}
