package net.osgi.jp2p.partial;

import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

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
