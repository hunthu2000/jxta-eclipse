package net.jp2p.container.persistence;

import net.jp2p.container.properties.IJp2pProperties;

public interface IPersistedProperties<T extends Object> {

	/**
	 * Get the persisted property with the given id, if it exists
	 * @param id
	 * @return
	 */
	public abstract T getProperty(IJp2pProperties id);

	public boolean setProperty(IJp2pProperties id, T value);

}