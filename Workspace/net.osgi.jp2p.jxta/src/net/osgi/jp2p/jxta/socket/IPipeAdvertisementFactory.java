package net.osgi.jp2p.jxta.socket;

import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.properties.IJp2pProperties;

public interface IPipeAdvertisementFactory {

	public enum Properties implements IJp2pProperties{
		SOCKET_ID,
		NAME,
		TYPE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
	}
	
	public enum PipeTypes{
		PROPAGATE_TYPE,
		UNICAST_TYPE,
		UNICAST_SECURE_TYPE;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}		
	}

}