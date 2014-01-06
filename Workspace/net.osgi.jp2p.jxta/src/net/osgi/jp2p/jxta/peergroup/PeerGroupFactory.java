/*******************************************************************************
 * Copyright (c) 2013 Condast and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kees Pieters - initial API and implementation
 *******************************************************************************/
package net.osgi.jp2p.jxta.peergroup;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.jxta.netpeergroup.NetPeerGroupPropertySource;
import net.osgi.jp2p.utils.Utils;
import net.osgi.jp2p.jxta.peergroup.PeerGroupFactory;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.AbstractPeerGroupProviderPropertySource.PeerGroupDirectives;

public class PeerGroupFactory extends AbstractComponentFactory<PeerGroup> 
{
	private PeerGroup parentPeerGroup;
	
	public PeerGroupFactory( ContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent) {
		super( container, parent );
	}

	@Override
	public String getComponentName() {
		return JxtaComponents.PEERGROUP_SERVICE.toString();
	}
	
	@Override
	protected PeerGroupPropertySource onCreatePropertySource() {
		return new PeerGroupPropertySource( super.getParentSource());
	}

	@Override
	protected IJp2pComponent<PeerGroup> onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		return null;//parentPeerGroup.newGroup(null);
	}
	
	/**
	 * Extract the peergroup from the given factory, or return null if no peergroup is present 
	 * @param factory
	 * @return
	 */
	public static PeerGroup getPeerGroup( IComponentFactory<?> factory ){
		Object component = factory.getComponent();
		PeerGroup peergroup = null;
		if(  component instanceof PeerGroup )
			peergroup = (PeerGroup) component;
		else if( component instanceof IJp2pComponent ){
			IJp2pComponent<?> comp = (IJp2pComponent<?>) component;
			peergroup = (PeerGroup) comp.getModule();		
		}
		return peergroup;
	}
	
	/**
	 * Returns true if the factory contasins the correct peergroup
	 * @param factory
	 * @return
	 */
	public static boolean isCorrectPeerGroup( IJp2pPropertySource<?> current, IComponentFactory<?> factory ){
		IJp2pPropertySource<?> ancestor = DiscoveryPropertySource.findPropertySource( current, PeerGroupDirectives.PEERGROUP );
		String peergroupName = null;
		if( ancestor != null ){
			peergroupName = ancestor.getDirective(PeerGroupDirectives.PEERGROUP);
		}
		if( Utils.isNull( peergroupName ))
			peergroupName = NetPeerGroupPropertySource.S_NET_PEER_GROUP;
		PeerGroup peergroup = PeerGroupFactory.getPeerGroup(factory);
		if( peergroup == null )
			return false;
		return ( peergroupName.toLowerCase().equals( peergroup.getPeerGroupName().toLowerCase()));	
	}

	/**
	 * Return the first peergroup along the ancestors, or the netpeertgroup if none were found
	 * @param current
	 * @param factory
	 * @return
	 */
	public static String findAncestorPeerGroup( IJp2pPropertySource<?> current ){
		String peergroup = PeerGroupPropertySource.findFirstAncestorDirective(current, PeerGroupDirectives.PEERGROUP );
		if( Utils.isNull( peergroup ))
			return NetPeerGroupPropertySource.S_NET_PEER_GROUP;
		else
			return peergroup;
	}
}