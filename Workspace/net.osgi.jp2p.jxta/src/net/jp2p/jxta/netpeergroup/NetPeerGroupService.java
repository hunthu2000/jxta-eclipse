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
package net.jp2p.jxta.netpeergroup;

import net.jp2p.container.component.AbstractJp2pService;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.jxta.netpeergroup.NetPeerGroupFactory;
import net.jp2p.jxta.peergroup.PeerGroupPreferences;
import net.jxta.peergroup.PeerGroup;
import net.jxta.compatibility.platform.NetworkManager;

public class NetPeerGroupService extends AbstractJp2pService<PeerGroup>{

	private NetworkManager manager;

	public NetPeerGroupService( NetPeerGroupFactory factory, NetworkManager manager ) {
		super(( IJp2pWritePropertySource<IJp2pProperties> ) factory.getPropertySource(), null );
		this.manager = manager;
	}

	@Override
	protected void activate() {
		try {
			PeerGroupPreferences preferences = new PeerGroupPreferences(( IJp2pWritePropertySource<IJp2pProperties> )super.getPropertySource() );
			manager.setPeerID( preferences.getPeerID());
			PeerGroup peergroup = manager.startNetwork();
			super.setModule( peergroup );
			super.activate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void deactivate() {
		manager.stopNetwork();
	}
	
	
}