package net.osgi.jp2p.properties;

public interface IManagedPropertyListener<T extends Object, U extends Object> {

	public void notifyValueChanged( ManagedPropertyEvent<T,U> event );
}
