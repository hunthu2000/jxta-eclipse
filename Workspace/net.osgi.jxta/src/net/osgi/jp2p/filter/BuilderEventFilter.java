package net.osgi.jp2p.filter;

import net.osgi.jp2p.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;

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
