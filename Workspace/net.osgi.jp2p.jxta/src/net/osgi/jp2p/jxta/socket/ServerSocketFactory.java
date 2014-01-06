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
package net.osgi.jp2p.jxta.socket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaServerSocket;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.component.Jp2pComponent;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.utils.IOUtils;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.jxta.socket.IServerSocketFactory;
import net.osgi.jp2p.jxta.socket.SocketPipeAdvertisementFactory;

public class ServerSocketFactory extends AbstractComponentFactory<JxtaServerSocket> implements IServerSocketFactory {

	public static final String S_JXSE_SERVER_SOCKET_SERVICE = "JxtaServerSocketService";	
	
	private NetworkManager manager;
	private PipeAdvertisementFactory pipeFactory;

	public ServerSocketFactory( ContainerBuilder container, NetworkManager manager ) {
		super( container );
		this.manager = manager;
		this.fillDefaultValues();
	}

	protected void fillDefaultValues() {
		//super.getPropertySource().setProperty( Properties.TIME_OUT, 10 );
		//super.getPropertySource().setProperty( Properties.SO_TIME_OUT, 0 );
	}

	@Override
	public String getComponentName() {
		return S_JXSE_SERVER_SOCKET_SERVICE;
	}	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected IJp2pComponent<JxtaServerSocket> onCreateComponent(IJp2pPropertySource<IJp2pProperties> properties) {
		this.pipeFactory = new SocketPipeAdvertisementFactory(super.getBuilder());
		JxtaServerSocket socket = this.createSocket();
		super.setCompleted(true);
		return new Jp2pComponent(  super.getPropertySource(),socket );
	}
		
	/**
	 * Create the socket
	 * @param manager
	 * @return
	 */
	private JxtaServerSocket createSocket() {
		PipeAdvertisement pipeAd = this.pipeFactory.getComponent().getModule();
		JxtaServerSocket serverSocket = null;
		try {
			IJp2pPropertySource<IJp2pProperties> source = super.getPropertySource();
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

	@Override
	protected IJp2pPropertySource<IJp2pProperties> onCreatePropertySource() {
		// TODO Auto-generated method stub
		return null;
	}
}