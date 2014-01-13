package net.osgi.jp2p.filter;

import net.osgi.jp2p.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;

public abstract class AbstractBuilderEventFilter<T extends Object> extends
		AbstractComponentFactoryFilter<T> {

	private BuilderEvents event;
	
	public AbstractBuilderEventFilter( BuilderEvents event, IComponentFactory<T> factory) {
		super(factory);
		this.event = event;
	}

	protected BuilderEvents getEvent() {
		return event;
	}

	/**
	 * Is called when the correct builder event is given. Should return true if the filter conditions are met
	 * @param event
	 * @return
	 */
	protected abstract boolean onCorrectBuilderEvent(ComponentBuilderEvent<?> event);
	
	@Override
	public boolean onAccept(ComponentBuilderEvent<?> event) {
		if( !this.event.equals( event.getBuilderEvent() ))
			return false;
		return this.onCorrectBuilderEvent(event);
	}

}
