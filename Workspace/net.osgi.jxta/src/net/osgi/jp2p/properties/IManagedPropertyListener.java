package net.osgi.jp2p.properties;

import net.osgi.jp2p.utils.StringStyler;

public interface IManagedPropertyListener<T extends Object, U extends Object> {

	public enum PropertyEvents{
		DEFAULT_VALUE_SET,
		VALUE_CHANGED;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
	}
	public void notifyValueChanged( ManagedPropertyEvent<T,U> event );
}
