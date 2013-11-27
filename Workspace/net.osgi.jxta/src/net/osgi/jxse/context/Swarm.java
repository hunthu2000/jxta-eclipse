package net.osgi.jxse.context;

import java.util.Collection;
import java.util.HashSet;

import net.jxta.peergroup.PeerGroup;

public class Swarm {

	private Collection<PeerGroup> peergroups;
	
	public Swarm() {
		peergroups = new HashSet<PeerGroup>();
	}
	
	public boolean addPeerGroup( PeerGroup peergroup ){
		return this.peergroups.add( peergroup );
	}

	public boolean removePeerGroup( PeerGroup peergroup ){
		return this.peergroups.remove( peergroup );
	}
	
	public int size(){
		return this.peergroups.size();
	}

}
