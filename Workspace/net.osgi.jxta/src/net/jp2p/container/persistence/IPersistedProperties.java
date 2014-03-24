package net.jp2p.container.persistence;

import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IPropertyConvertor;

public interface IPersistedProperties<T,U extends Object> {

	/**
	 * set the convertor for this property
	 * @param context
	 */
	public void setConvertor( IPropertyConvertor<T,U> convertor );
	
	/**
	 * Clear the property
	 * @param source
	 */
	public void clear( IJp2pPropertySource<IJp2pProperties> source );
	
	/**
	 * Get the persisted property with the given id, if it exists
	 * @param id
	 * @return
	 */
	public abstract T getProperty( IJp2pPropertySource<IJp2pProperties> source, IJp2pProperties id);

	public boolean setProperty(IJp2pPropertySource<IJp2pProperties> source, IJp2pProperties id, T value);

}