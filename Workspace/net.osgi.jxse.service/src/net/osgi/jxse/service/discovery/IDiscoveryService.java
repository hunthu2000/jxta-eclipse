package net.osgi.jxse.service.discovery;

import net.osgi.jxse.utils.StringStyler;

public interface IDiscoveryService {

	public enum Services{
		DISCOVERY_SERVICE,
		ENDPOINT_SERVICE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}	
	}
	public enum DiscoveryProperties{
		STATUS,
		MODE,
		WAIT_TIME,
		PEER_ID,
		ADVERTISEMENT_TYPE,
		ATTRIBUTE,
		WILDCARD,
		THRESHOLD;

	@Override
	public String toString() {
		return StringStyler.prettyString( super.toString() );
	}}
	
	public enum Mode{
		DISCOVERY,
		PUBLISH,
		DISCOVERY_AND_PUBLISH;

	@Override
	public String toString() {
		return StringStyler.prettyString( super.toString() );
	}}
}