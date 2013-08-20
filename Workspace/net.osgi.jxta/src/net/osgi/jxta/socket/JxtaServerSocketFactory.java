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
package net.osgi.jxta.socket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaServerSocket;
import net.osgi.jxta.advertisement.PipeAdvertisementFactory;
import net.osgi.jxta.factory.AbstractServiceComponentFactory;
import net.osgi.jxta.utils.IOUtils;
import net.osgi.jxta.utils.StringStyler;

public class JxtaServerSocketFactory extends AbstractServiceComponentFactory<JxtaServerSocket> {

	public static final String S_JXSE_SERVER_SOCKET_SERVICE = "JxtaServerSocketService";
	
	
	public enum Properties{
		TIME_OUT,
		SO_TIME_OUT;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	
	private NetworkManager manager;
	private PipeAdvertisementFactory pipeFactory;

	public JxtaServerSocketFactory(NetworkManager manager ) {
		super( Components.JXTA_SERVER_SOCKET );
		this.manager = manager;
		this.fillDefaultValues();
	}

	@Override
	protected void fillDefaultValues() {
		super.addProperty( Properties.TIME_OUT, 10 );
		super.addProperty( Properties.SO_TIME_OUT, 0 );
	}
	
	@Override
	protected void onParseDirectivePriorToCreation(Directives directive,
			String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onParseDirectiveAfterCreation( JxtaServerSocket component, Directives directive, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected JxtaServerSocket onCreateModule() {
		this.pipeFactory = new SocketPipeAdvertisementFactory();
		JxtaServerSocket socket = this.createSocket();
		super.setCompleted(true);
		return socket;
	}
		
	/**
	 * Create the socket
	 * @param manager
	 * @return
	 */
	private JxtaServerSocket createSocket() {
		PipeAdvertisement pipeAd = this.pipeFactory.getModule();
		JxtaServerSocket serverSocket = null;
		try {
			serverSocket = new JxtaServerSocket( manager.getNetPeerGroup(), pipeAd, ( boolean )super.getProperty( Properties.TIME_OUT ));
			serverSocket.setSoTimeout(( int )super.getProperty( Properties.SO_TIME_OUT ));
			return serverSocket;
		} catch (IOException e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
			IOUtils.closeSocket( serverSocket );
		}
		return null;
	}
	
}