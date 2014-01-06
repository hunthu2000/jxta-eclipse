package net.osgi.jp2p.jxta.netpeergroup;

import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public class NetPeerGroupPropertySource extends PeerGroupPropertySource{
	
	public static final String S_NET_PEER_GROUP = "NetPeerGroup";
	
	public NetPeerGroupPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( JxtaComponents.NET_PEERGROUP_SERVICE.toString(), parent);
	}
}