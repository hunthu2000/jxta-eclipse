/*******************************************************************************
 * Copyright (c) 2013 Condast.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Condast - initial API and implementation
 ******************************************************************************/
package org.mortbay.jetty.provider;

import net.jxse.osgi.service.IServerStarter;

import org.eclipselabs.osgi.ds.broker.service.AbstractPalaver;
import org.eclipselabs.osgi.ds.broker.service.AbstractProvider;

public class ServerProvider extends AbstractProvider<String, Object, IServerStarter> {

	private static ServerProvider attendee = new ServerProvider();
	
	private IServerStarter starter;
	
	private ServerProvider() {
		super( new Palaver());
		//super.getPalaver().setClaimAttention(false);
	}
	
	public static ServerProvider getInstance(){
		return attendee;
	}

	/**
	 * Add a container and 
	 * @param container
	 */
	public void setContainer(IServerStarter starter) {
		if( starter == null )
			throw new NullPointerException();
		this.starter = starter;
		super.provide(starter);
	}

	@Override
	protected void onDataReceived( Object msg ){
		if(!( msg instanceof String ))
			return;
		if( this.starter != null )
			super.provide(starter);
	}	
}

/**
 * The palaver contains the conditions for attendees to create an assembly. In this case, the attendees must
 * pass a string identifier (the package id) and provide a token that is equal
 * @author Kees
 *
 */
class Palaver extends AbstractPalaver<String>{

	public static final String S_IJXTACONTAINER_PACKAGE_ID = "org.osgi.jxta.service.ijxtaservicecomponent";
	public static final String S_IJXTA_TOKEN = "org.osgi.jxse.server.token";
	
	protected Palaver() {
		super(S_IJXTACONTAINER_PACKAGE_ID);
	}

	@Override
	public String giveToken() {
		return S_IJXTA_TOKEN;
	}

	@Override
	public boolean confirm(Object token) {
		return ( token.equals(S_IJXTA_TOKEN ));
	}	
}
