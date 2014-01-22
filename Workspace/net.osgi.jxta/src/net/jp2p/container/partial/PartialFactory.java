package net.jp2p.container.partial;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.factory.AbstractComponentFactory;
import net.jp2p.container.factory.ComponentBuilderEvent;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;

public class PartialFactory<T extends Object> extends AbstractComponentFactory<T> {

	private String componentName;
	private boolean completed;
	
	public PartialFactory( IContainerBuilder container, String componentName, IJp2pPropertySource<IJp2pProperties> parentSource ){
		super( container, parentSource);
		this.componentName = componentName;
		this.completed = false;
	}

	@Override
	public String getComponentName() {
		return componentName;
	}

	@Override
	protected PartialPropertySource onCreatePropertySource() {
		return new PartialPropertySource( this.componentName, (IJp2pPropertySource<IJp2pProperties>) super.getParentSource() );
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
	protected IJp2pComponent<T> onCreateComponent(IJp2pPropertySource<IJp2pProperties> properties) {
		// TODO Auto-generated method stub
		return null;
	}
}
