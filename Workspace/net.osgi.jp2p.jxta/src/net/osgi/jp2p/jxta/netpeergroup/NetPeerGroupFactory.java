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
package net.osgi.jp2p.jxta.netpeergroup;

import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.factory.AbstractComponentDependencyFactory;
import net.osgi.jp2p.filter.ComponentFilter;
import net.osgi.jp2p.filter.IComponentFactoryFilter;
import net.osgi.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.osgi.jp2p.jxta.netpeergroup.NetPeerGroupService;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public class NetPeerGroupFactory extends AbstractComponentDependencyFactory<PeerGroup, IJp2pComponent<NetworkManager>>{

	public NetPeerGroupFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent ) {
		super( container, parent );
	}

	@Override
	public String getComponentName() {
		return JxtaComponents.NET_PEERGROUP_SERVICE.toString();
	}	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected IComponentFactoryFilter createFilter() {
		return new ComponentFilter( BuilderEvents.FACTORY_COMPLETED, JxtaComponents.NETWORK_MANAGER.toString(), this );
	}

	@Override
	public PeerGroupPropertySource onCreatePropertySource() {
		PeerGroupPropertySource source = new PeerGroupPropertySource( PeerGroupPropertySource.S_NET_PEER_GROUP, super.getParentSource());
		return source;
	}
	
	@Override
	protected IJp2pComponent<PeerGroup> onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		return new NetPeerGroupService( this, (NetworkManager) super.getDependency().getModule() );
	}
}