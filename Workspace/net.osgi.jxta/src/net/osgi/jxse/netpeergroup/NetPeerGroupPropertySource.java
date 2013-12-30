package net.osgi.jxse.netpeergroup;

import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.PeerGroupPropertySource;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class NetPeerGroupPropertySource extends PeerGroupPropertySource{
	
	public static final String S_NET_PEER_GROUP = "NetPeerGroup";
	
	public NetPeerGroupPropertySource( IJxsePropertySource<IJxseProperties> parent) {
		super( Components.NET_PEERGROUP_SERVICE.toString(), parent);
	}
}