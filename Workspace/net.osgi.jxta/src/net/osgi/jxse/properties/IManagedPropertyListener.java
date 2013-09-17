package net.osgi.jxse.properties;

public interface IManagedPropertyListener<T extends Enum<T>, U extends Object> {

	public void notifyValueChanged( ManagedPropertyEvent<T,U> event );
}
