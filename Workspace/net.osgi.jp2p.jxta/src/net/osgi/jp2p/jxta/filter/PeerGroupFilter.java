package net.osgi.jp2p.jxta.filter;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jp2p.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.filter.AbstractComponentFactoryFilter;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource;
import net.osgi.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.osgi.jp2p.jxta.peergroup.PeerGroupFactory;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource.PeerGroupDirectives;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.utils.Utils;

public class PeerGroupFilter<T extends Object> extends AbstractComponentFactoryFilter<T> {

	private String peergroupName;
	
	private PeerGroup peergroup;
	
	public PeerGroupFilter( IComponentFactory<T> factory, String peergroupName ) {
		super( factory );
		this.peergroupName = peergroupName;
	}

	public PeerGroupFilter( IComponentFactory<T> factory ) {
		super( factory );
		IJp2pPropertySource<?> ancestor = DiscoveryPropertySource.findPropertySource( factory.getPropertySource(), PeerGroupDirectives.PEERGROUP );
		if( ancestor != null ){
			peergroupName = ancestor.getDirective(PeerGroupDirectives.PEERGROUP);
		}
		if( Utils.isNull( peergroupName ))
			peergroupName = PeerGroupPropertySource.S_NET_PEER_GROUP;
	}


	public PeerGroup getPeergroup() {
		return peergroup;
	}

	@Override
	public boolean onAccept(ComponentBuilderEvent<?> event) {
		if( !BuilderEvents.COMPONENT_STARTED.equals( event.getBuilderEvent()))
			return false;
		PeerGroup pg = PeerGroupFactory.getPeerGroup(event.getFactory());
		if( pg == null )
			return false;
		peergroup = pg;
		return ( peergroupName.toLowerCase().equals( peergroup.getPeerGroupName().toLowerCase()));	
	}

	/**
	 * Returns true if the factory contains the correct peergroup
	 * @param factory
	 * @return
	 */
	public static boolean isCorrectPeerGroup( IJp2pPropertySource<?> current, IComponentFactory<?> factory ){
		if( !AbstractComponentFactory.isComponentFactory( JxtaComponents.PEERGROUP_SERVICE, factory ) && 
				!AbstractComponentFactory.isComponentFactory( JxtaComponents.NET_PEERGROUP_SERVICE, factory ))
			return false;
		IJp2pPropertySource<?> ancestor = DiscoveryPropertySource.findPropertySource( current, PeerGroupDirectives.PEERGROUP );
		String peergroupName = null;
		if( ancestor != null ){
			peergroupName = ancestor.getDirective(PeerGroupDirectives.PEERGROUP);
		}
		if( Utils.isNull( peergroupName ))
			peergroupName = PeerGroupPropertySource.S_NET_PEER_GROUP;
		PeerGroup peergroup = PeerGroupFactory.getPeerGroup(factory);
		if( peergroup == null )
			return false;
		return ( peergroupName.toLowerCase().equals( peergroup.getPeerGroupName().toLowerCase()));	
	}

}
