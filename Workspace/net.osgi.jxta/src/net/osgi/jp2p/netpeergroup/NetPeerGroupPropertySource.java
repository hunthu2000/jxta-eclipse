package net.osgi.jp2p.netpeergroup;

import net.osgi.jp2p.factory.IComponentFactory.Components;
import net.osgi.jp2p.peergroup.PeerGroupPropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public class NetPeerGroupPropertySource extends PeerGroupPropertySource{
	
	public static final String S_NET_PEER_GROUP = "NetPeerGroup";
	
	public NetPeerGroupPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( Components.NET_PEERGROUP_SERVICE.toString(), parent);
	}
}