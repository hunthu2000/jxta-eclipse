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
package net.osgi.jxse.netpeergroup;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.component.AbstractJxseService;

public class NetPeerGroupService extends AbstractJxseService<PeerGroup>{

	public static final String S_NETPEERGROUP_SERVICE = "Jxse Net Peergroup Service";

	private NetworkManager manager;

	public NetPeerGroupService( NetPeerGroupFactory factory, NetworkManager manager ) {
		super( factory.getPropertySource().getBundleId(), S_NETPEERGROUP_SERVICE );
		this.manager = manager;
	}

	@Override
	protected void activate() {
		try {
			PeerGroup peergroup = manager.startNetwork();
			super.setComponent( peergroup );
		} catch (PeerGroupException | IOException e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
		}
		super.activate();
	}

	@Override
	protected void deactivate() {
		manager.stopNetwork();
	}
	
	
}