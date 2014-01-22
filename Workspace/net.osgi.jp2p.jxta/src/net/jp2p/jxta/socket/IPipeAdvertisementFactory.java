package net.jp2p.jxta.socket;

import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.utils.StringStyler;

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