package net.jp2p.container.properties;

public interface IPropertyConvertor<T, U extends Object> {

	/**
	 * Convert the given id to value T
	 * @param id
	 * @return
	 */
	public T convertFrom(IJp2pProperties id);

	/**
	 * Convert the given value T to the correct internal value
	 * @param id
	 * @param value
	 * @return
	 */
	public U convertTo( IJp2pProperties id, T value );
	
	/**
	 * Convert the given value T to the correct internal value, and store it in the property source.
	 * returns true if the value is stored
	 * 
	 * @param id
	 * @param value
	 */
	public boolean setPropertyFromConverion( IJp2pProperties id, T value);
}
