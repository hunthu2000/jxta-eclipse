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
package net.osgi.jxse.network;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.component.AbstractJxseService;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.peergroup.IPeerGroupProperties.PeerGroupProperties;

public class NetPeerGroupService extends AbstractJxseService<PeerGroup, PeerGroupProperties, IJxseDirectives>{

	public static final String S_NETWORK_MANAGER = "Jxta Network Manager";

	private NetworkManager manager;

	public NetPeerGroupService( IComponentFactory<PeerGroup, PeerGroupProperties, IJxseDirectives> factory ) {
		super( factory );
		this.manager = (NetworkManager) factory.getModule();
	}

	public NetPeerGroupService( String bundleId, String identifier, NetworkManager manager) {
		super( bundleId, identifier, S_NETWORK_MANAGER );
		this.manager = manager;
	}

	public Object getProperty( PeerGroupProperties key ){
		return super.getProperty(key);
	}
	
	public NetworkManager getNetworkManager(){
		return manager;
	}

	@Override
	protected void activate() {
		try {
			PeerGroup peergroup = manager.startNetwork();
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
		manager.stopNetwork();
	}
}