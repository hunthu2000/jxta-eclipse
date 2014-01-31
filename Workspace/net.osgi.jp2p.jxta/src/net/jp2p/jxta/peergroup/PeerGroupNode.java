package net.jp2p.jxta.peergroup;

import java.util.Stack;

import net.jp2p.container.utils.SimpleNode;
import net.jp2p.jxta.advertisement.IAdvertisementProvider;
import net.jxta.document.Advertisement;
import net.jxta.peergroup.PeerGroup;

public class PeerGroupNode extends SimpleNode<PeerGroup, PeerGroup> implements
		IAdvertisementProvider {

	public PeerGroupNode(PeerGroup data) {
		super(data);
	}

	@Override
	public Advertisement[] getAdvertisements() {
		PeerGroup peergroup = super.getData();
		Stack<Advertisement> stack = new Stack<Advertisement>();
		stack.push( peergroup.getImplAdvertisement() );
		stack.push( peergroup.getPeerAdvertisement() );
		stack.push( peergroup.getPeerGroupAdvertisement() );
		return stack.toArray( new Advertisement[ stack.size()]);
	}

}
