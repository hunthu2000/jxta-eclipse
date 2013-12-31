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
package net.osgi.jp2p.service.sockets;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaSocket;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.component.Jp2pComponent;
import net.osgi.jp2p.socket.SocketFactory;
import net.osgi.jp2p.utils.IOUtils;

public class JxtaSocketComponent extends Jp2pComponent<JxtaSocket> {

	public static final String S_SERVER_SOCKET = "JXTA ServerSocket";

	public enum JxseSocketProperties{
		
	}
	
	private IJp2pComponent<PipeAdvertisement> pipeAd;

	
	public JxtaSocketComponent( IJp2pComponent<NetworkManager> manager, IJp2pComponent<PipeAdvertisement> pipeAd, 
			Properties properties ) {
		super( manager, null );
		this.pipeAd = pipeAd;
	}

	@Override
	public boolean isRoot() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JxtaSocket getModule() {
		JxtaSocket socket = null;
		 IJp2pComponent<NetworkManager> manager = (IJp2pComponent<NetworkManager> )super.getParent();
		try {
			return new JxtaSocket( manager.getModule().getNetPeerGroup(), null, pipeAd.getModule(), ( int )super.getProperty( SocketFactory.Properties.TIME_OUT ));
		} catch (Exception e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
			IOUtils.closeSocket( socket );
		}
		return null;
	}
}
