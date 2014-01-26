package net.jp2p.container.factory.filter;

import net.jp2p.container.builder.ICompositeBuilderListener.BuilderEvents;
import net.jp2p.container.factory.IComponentFactory;

public class ComponentCreateFilter<T, U extends Object> extends
		AbstractComponentFilter<T, U> {

	private String componentName;
	
	public ComponentCreateFilter(BuilderEvents event, String componentName, IComponentFactory<T> factory ) {
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
