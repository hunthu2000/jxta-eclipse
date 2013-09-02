package net.osgi.jxse.service.peergroup;

import net.osgi.jxse.utils.StringStyler;

public interface IPeerGroupProperties {

	public enum PeerGroupProperties{
		NAME,
		IDENTIFIER,
		PUBLISH,
		PEER_GROUP_ID,
		RENDEZ_VOUS;

	@Override
	public String toString() {
		return StringStyler.prettyString( super.toString() );
	}}

}
