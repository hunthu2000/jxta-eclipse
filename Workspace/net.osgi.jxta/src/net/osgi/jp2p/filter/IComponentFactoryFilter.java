package net.osgi.jp2p.filter;

import net.osgi.jp2p.factory.ComponentBuilderEvent;

public interface IComponentFactoryFilter {

	/**
	 * Returns true if the conditions of the filter are met. Note that this can be 
	 * the result of multiple calls
	 * @param event
	 * @return
	 */
	public boolean accept( ComponentBuilderEvent<?> event);
	
	/**
	 * returns the result of the accept method, and is changed at the start of a new trial
	 * @return
	 */
	public boolean hasAccepted();
}
