package net.jp2p.container.persistence;

import net.jp2p.container.context.IJp2pContext;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;

public interface IPersistedProperties<T extends Object> {

	public void setContext( IJp2pContext<?> context );
	/**
	 * Get the persisted property with the given id, if it exists
	 * @param id
	 * @return
	 */
	public abstract T getProperty( IJp2pPropertySource<IJp2pProperties> source, IJp2pProperties id);

	public boolean setProperty(IJp2pPropertySource<IJp2pProperties> source, IJp2pProperties id, T value);

}