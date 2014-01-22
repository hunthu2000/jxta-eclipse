package net.jp2p.container;

import java.util.Collection;
import java.util.HashSet;

public class Swarm<T extends Object> {

	private Collection<T> peergroups;
	
	public Swarm() {
		peergroups = new HashSet<T>();
	}
	
	public boolean addPeerGroup( T peergroup ){
		return this.peergroups.add( peergroup );
	}

	public boolean removePeerGroup( T peergroup ){
		return this.peergroups.remove( peergroup );
	}
	
	public T getPeerGroup( String peergroupName ){
		for( T peergroup: this.peergroups ){
			if( peergroup.toString().equals( peergroupName ))
				return peergroup;
		}
		return null;
	}
	
	//public T[] getPeerGroups(){
	//	return this.peergroups.toArray( new Object[1] );
	//}
	
	public int size(){
		return this.peergroups.size();
	}

}
