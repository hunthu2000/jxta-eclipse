package net.osgi.jxse.socket;

import net.osgi.jxse.utils.StringStyler;

public interface ISocketFactory {


	public enum Properties{
		TIME_OUT,
		SO_TIME_OUT,
		WAIT_FOR_RENDEZ_VOUS;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
}