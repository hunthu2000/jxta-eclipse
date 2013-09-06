package net.osgi.jxse.advertisement;

import net.osgi.jxse.utils.StringStyler;

public interface IPipeAdvertisementFactory {

	public enum Properties{
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