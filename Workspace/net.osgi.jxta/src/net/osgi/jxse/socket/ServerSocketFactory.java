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
package net.osgi.jxse.socket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaServerSocket;
import net.osgi.jxse.advertisement.PipeAdvertisementFactory;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.IOUtils;

public class ServerSocketFactory extends AbstractComponentFactory<JxtaServerSocket, net.osgi.jxse.socket.IServerSocketFactory.Properties, net.osgi.jxse.factory.IComponentFactory.Directives> implements IServerSocketFactory {

	public static final String S_JXSE_SERVER_SOCKET_SERVICE = "JxtaServerSocketService";	
	
	private NetworkManager manager;
	private PipeAdvertisementFactory pipeFactory;

	public ServerSocketFactory(NetworkManager manager ) {
		super( null );
		this.manager = manager;
		this.fillDefaultValues();
	}

	protected void fillDefaultValues() {
		super.getPropertySource().setProperty( Properties.TIME_OUT, 10 );
		super.getPropertySource().setProperty( Properties.SO_TIME_OUT, 0 );
	}
	
	@Override
	protected void onParseDirectivePriorToCreation(Directives directive,
			Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onParseDirectiveAfterCreation( JxtaServerSocket component, Directives directive, Object value) {
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
			IJxsePropertySource<Properties, Directives> source = super.getPropertySource();
			serverSocket = new JxtaServerSocket( manager.getNetPeerGroup(), pipeAd, ( boolean )source.getProperty( Properties.TIME_OUT ));
			serverSocket.setSoTimeout(( int )source.getProperty( Properties.SO_TIME_OUT ));
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