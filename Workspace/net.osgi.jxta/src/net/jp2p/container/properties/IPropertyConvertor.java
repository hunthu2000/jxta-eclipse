package net.jp2p.container.properties;

public interface IPropertyConvertor<T, U extends Object> {

	public T convertFrom(IJp2pProperties id);

	public U convertTo( IJp2pProperties id, T value );
}
