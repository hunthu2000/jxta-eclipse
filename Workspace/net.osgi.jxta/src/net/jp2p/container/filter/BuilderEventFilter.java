package net.jp2p.container.filter;

import net.jp2p.container.builder.ICompositeBuilderListener.BuilderEvents;
import net.jp2p.container.factory.ComponentBuilderEvent;
import net.jp2p.container.factory.IComponentFactory;

public class BuilderEventFilter<T extends Object> extends
		AbstractComponentFactoryFilter<T> {

	private BuilderEvents event;
	
	public BuilderEventFilter( BuilderEvents event, IComponentFactory<T> factory) {
		super(factory);
		this.event = event;
	}

	protected BuilderEvents getEvent() {
		return event;
	}

	@Override
	public boolean onAccept(ComponentBuilderEvent<?> event) {
		return this.event.equals( event.getBuilderEvent() );
	}

}
