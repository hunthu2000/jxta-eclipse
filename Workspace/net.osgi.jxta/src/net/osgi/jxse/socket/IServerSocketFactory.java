package net.osgi.jxse.socket;

import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.utils.StringStyler;

public interface IServerSocketFactory{

	public enum Properties  implements IJxseProperties{
		TIME_OUT,
		SO_TIME_OUT;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	

}