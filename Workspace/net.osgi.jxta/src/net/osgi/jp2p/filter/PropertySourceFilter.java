package net.osgi.jp2p.filter;

import net.osgi.jp2p.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;

public class PropertySourceFilter<T,U extends Object> extends AbstractBuilderEventFilter<T> {
	
	public static final String S_ERR_INVALID_BUILDER_EVENT = "Invalid builder event. This filter applies for Property source creatyion only";
	
	private String componentName;
	
	public PropertySourceFilter( String componentName, IComponentFactory<T> factory) {
		super( BuilderEvents.PROPERTY_SOURCE_CREATED, factory);
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