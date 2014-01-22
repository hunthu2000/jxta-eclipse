package net.jp2p.jxta.socket;

import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.utils.StringStyler;

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