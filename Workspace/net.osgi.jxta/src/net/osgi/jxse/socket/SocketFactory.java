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
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.JxseComponent;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class SocketFactory extends AbstractComponentFactory<JxtaSocket> implements ISocketFactory{

	public static final String S_JXSE_SOCKET_SERVICE = "JxtaSocketService";
	
	private NetworkManager manager;
	private PipeAdvertisementFactory pipeFactory;

	public SocketFactory( BuilderContainer container, NetworkManager manager ) {
		super( container );
		this.manager = manager;
		this.fillDefaultValues();
	}
	
	@Override
	public String getComponentName() {
		return S_JXSE_SOCKET_SERVICE;
	}


	public NetworkManager getManager() {
		return manager;
	}


	public IPipeAdvertisementFactory getPipeFactory() {
		return pipeFactory;
	}


	protected void fillDefaultValues() {
		IJxseWritePropertySource<IJxseProperties> source = (IJxseWritePropertySource<IJxseProperties>) super.getPropertySource();
		source.setProperty( Properties.TIME_OUT, 5000 );
		source.setProperty( Properties.SO_TIME_OUT, 0 );
		source.setProperty( Properties.WAIT_FOR_RENDEZ_VOUS, false );
	}

	
	@Override
	protected void onParseDirectivePriorToCreation(IJxseDirectives directive, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IJxseComponent<JxtaSocket> onCreateComponent( IJxsePropertySource<IJxseProperties> properties) {
		this.pipeFactory = new SocketPipeAdvertisementFactory( super.getContainer());
		JxtaSocket socket = this.createSocket();
		//super.setCompleted(true);
		return new JxseComponent( socket );
	}
		
	/**
	 * Create the socket
	 * @param manager
	 * @return
	 */
	private JxtaSocket createSocket() {
		PipeAdvertisement pipeAd = this.pipeFactory.getComponent().getModule();
		return null;//TODO Change to include parent factory: new JxtaSocketComponent( manager, pipeAd, super.getProperties() );
	}


	@Override
	protected IJxsePropertySource<IJxseProperties> onCreatePropertySource() {
		// TODO Auto-generated method stub
		return null;
	}
}