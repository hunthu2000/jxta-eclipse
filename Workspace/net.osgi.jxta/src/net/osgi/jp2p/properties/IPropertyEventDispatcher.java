package net.osgi.jp2p.properties;

public interface IPropertyEventDispatcher {

	public abstract void addPropertyListener(
			IManagedPropertyListener<IJp2pProperties, ?> listener);

	public abstract void removePropertyListener(
			IManagedPropertyListener<IJp2pProperties, ?> listener);

}