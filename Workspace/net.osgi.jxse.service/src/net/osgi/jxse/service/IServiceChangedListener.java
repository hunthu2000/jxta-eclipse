package net.osgi.jxse.service;

import net.osgi.jxse.utils.StringStyler;

public interface IServiceChangedListener {

	public enum ServiceChange{
		CHILD_ADDED,
		CHILD_REMOVED,
		STATUS_CHANGE,
		COMPONENT_EVENT;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	
	public void notifyServiceChanged( ServiceChangedEvent event );
}
