package net.jp2p.jxta.context;

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
	
	public PeerGroup getPeerGroup( String peergroupName ){
		for( PeerGroup peergroup: this.peergroups ){
			if( peergroup.getPeerGroupName().equals( peergroupName ))
				return peergroup;
		}
		return null;
	}
	
	public PeerGroup[] getPeerGroups(){
		return this.peergroups.toArray( new PeerGroup[1] );
	}
	
	public int size(){
		return this.peergroups.size();
	}

}
