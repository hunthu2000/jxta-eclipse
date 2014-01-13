package net.osgi.jp2p.filter;

import net.osgi.jp2p.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jp2p.factory.IComponentFactory;

public class ComponentFilter<T, U extends Object> extends
		AbstractComponentFilter<T, U> {

	private String componentName;
	
	public ComponentFilter(BuilderEvents event, String componentName, IComponentFactory<T> factory ) {
		super(event, factory);
		this.componentName = componentName;
	}


	@Override
	public U getComponent() {
		return super.getComponent();
	}


	@Override
	protected boolean checkComponent(IComponentFactory<U> factory) {
		return ( componentName.equals( factory.getComponentName()));
	}

}
