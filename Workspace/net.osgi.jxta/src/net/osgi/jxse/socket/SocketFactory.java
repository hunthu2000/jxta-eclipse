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

import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaSocket;
import net.osgi.jxse.advertisement.IPipeAdvertisementFactory;
import net.osgi.jxse.advertisement.PipeAdvertisementFactory;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Directives;
import net.osgi.jxse.preferences.properties.IJxsePropertySource;

public class SocketFactory extends AbstractComponentFactory<JxtaSocket, ISocketFactory.Properties, Directives> implements ISocketFactory{

	public static final String S_JXSE_SOCKET_SERVICE = "JxtaSocketService";
	
	private NetworkManager manager;
	private PipeAdvertisementFactory pipeFactory;

	public SocketFactory( NetworkManager manager ) {
		super( null );
		this.manager = manager;
		this.fillDefaultValues();
	}

	
	public NetworkManager getManager() {
		return manager;
	}


	public IPipeAdvertisementFactory getPipeFactory() {
		return pipeFactory;
	}


	protected void fillDefaultValues() {
		IJxsePropertySource<Properties, Directives> source = super.getPropertySource();
		source.setProperty( Properties.TIME_OUT, 5000 );
		source.setProperty( Properties.SO_TIME_OUT, 0 );
		source.setProperty( Properties.WAIT_FOR_RENDEZ_VOUS, false );
	}

	
	@Override
	protected void onParseDirectivePriorToCreation(Directives directive, Object value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void onParseDirectiveAfterCreation( JxtaSocket component, Directives directive, Object value) {}


	protected JxtaSocket onCreateModule() {
		this.pipeFactory = new SocketPipeAdvertisementFactory();
		JxtaSocket socket = this.createSocket();
		//super.setCompleted(true);
		return socket;
	}
		
	/**
	 * Create the socket
	 * @param manager
	 * @return
	 */
	private JxtaSocket createSocket() {
		PipeAdvertisement pipeAd = this.pipeFactory.getModule();
		return null;//TODO Change to include parent factory: new JxtaSocketComponent( manager, pipeAd, super.getProperties() );
	}	
}