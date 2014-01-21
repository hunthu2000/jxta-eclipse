package net.osgi.jp2p.persistence;

import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public interface IPersistedProperty<T extends Object> {

	/**
	 * set the property source
	 * @param source
	 */
	public void setPropertySource( IJp2pWritePropertySource<IJp2pProperties> source );
	
	/**
	 * Get the persisted property with the given id, if it exists
	 * @param id
	 * @return
	 */
	public abstract T getProperty(IJp2pProperties id);

	/**
	 * Set the property of a preference store
	 * @param id
	 * @param value
	 * @return
	 */
	public abstract boolean setProperty(IJp2pProperties id );

}