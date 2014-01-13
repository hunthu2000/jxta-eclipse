package net.osgi.jp2p.filter;

import net.osgi.jp2p.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;

public class FactoryFilter<T,U extends Object> extends AbstractBuilderEventFilter<T> {
	
	private String componentName;
	
	public FactoryFilter( BuilderEvents event, String componentName, IComponentFactory<T> factory) {
		super(event, factory);
		this.componentName = componentName;
	}

	@Override
	public IComponentFactory<T> getFactory() {
		return super.getFactory();
	}

	@Override
	protected boolean onCorrectBuilderEvent(ComponentBuilderEvent<?> event) {
		return ( componentName.equals( event.getFactory().getComponentName()));		
	}
}