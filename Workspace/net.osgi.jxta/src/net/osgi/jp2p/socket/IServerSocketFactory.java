package net.osgi.jp2p.socket;

import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.utils.StringStyler;

public interface IServerSocketFactory{

	public enum Properties  implements IJp2pProperties{
		TIME_OUT,
		SO_TIME_OUT;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	

}