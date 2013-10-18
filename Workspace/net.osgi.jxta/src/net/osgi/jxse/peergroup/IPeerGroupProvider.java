package net.osgi.jxse.peergroup;

import net.jxta.peergroup.PeerGroup;

public interface IPeerGroupProvider {

	public static final String S_NET_PEER_GROUP = "NetPeerGroup";
	
	/**
	 * Get the name of the (intended) peergroup
	 * @return
	 */
	public String getPeerGroupName();
	
	/**
	 * This supportive interface allows various objects to provide a peer goup.
	 * The interface does not define WHEN the peergroup is available.
	 * @return
	 */
	public PeerGroup getPeerGroup();
}
