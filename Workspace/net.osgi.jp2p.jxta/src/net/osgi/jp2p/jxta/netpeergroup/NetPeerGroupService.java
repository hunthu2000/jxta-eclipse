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
import net.osgi.jp2p.component.AbstractJp2pService;
import net.osgi.jp2p.jxta.netpeergroup.NetPeerGroupFactory;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPreferences;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class NetPeerGroupService extends AbstractJp2pService<PeerGroup>{

	public static final String S_NETPEERGROUP_SERVICE = "Jxse Net Peergroup Service";

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