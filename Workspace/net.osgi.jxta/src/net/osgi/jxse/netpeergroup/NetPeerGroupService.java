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
import net.osgi.jxse.component.AbstractJxseService;
import net.osgi.jxse.network.NetworkManagerFactory;
import net.osgi.jxse.peergroup.IPeerGroupProperties.PeerGroupProperties;

public class NetPeerGroupService extends AbstractJxseService<PeerGroup, PeerGroupProperties>{

	public static final String S_NETPEERGROUP_SERVICE = "Jxse Net Peergroup Service";

	private NetworkManagerFactory factory;

	public NetPeerGroupService( NetworkManagerFactory factory ) {
		super( factory.getPropertySource().getBundleId(), factory.getPropertySource().getIdentifier(), S_NETPEERGROUP_SERVICE );
		this.factory = factory;
	}

	public Object getProperty( PeerGroupProperties key ){
		return super.getProperty(key);
	}
	
	@Override
	protected void activate() {
		try {
			PeerGroup peergroup = factory.getModule().startNetwork();
			super.setModule( peergroup );
		} catch (PeerGroupException | IOException e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
		}
		super.activate();
	}

	@Override
	protected void deactivate() {
		factory.getModule().stopNetwork();
	}
}