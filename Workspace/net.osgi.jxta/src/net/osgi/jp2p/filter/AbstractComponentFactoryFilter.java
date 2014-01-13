package net.osgi.jp2p.filter;

import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;

public abstract class AbstractComponentFactoryFilter<T extends Object>
		implements IComponentFactoryFilter {

	private IComponentFactory<T> factory;
	private boolean accept;
	
	protected AbstractComponentFactoryFilter( IComponentFactory<T> factory ) {
		this.factory = factory;
	}

	protected IComponentFactory<T> getFactory() {
		return factory;
	}

	/**
	 * Provide the conditions on which the filter will accept the event
	 * @param event
	 * @return
	 */
	protected abstract boolean onAccept( ComponentBuilderEvent<?> event );
	
	@Override
	public boolean accept(ComponentBuilderEvent<?> event) {
		this.accept = this.onAccept(event);
		return accept;
	}

	@Override
	public boolean hasAccepted() {
		return accept;
	}
}
