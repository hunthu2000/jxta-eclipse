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
package net.osgi.jxse.service;

import org.eclipselabs.osgi.ds.broker.service.AbstractPalaver;
import org.eclipselabs.osgi.ds.broker.service.AbstractProvider;

import net.jxta.peergroup.PeerGroup;

public class PeerGroupProvider extends AbstractProvider<String, String, PeerGroup> {

	private static PeerGroupProvider attendee = new PeerGroupProvider();;
	
	private PeerGroup netPeerGroup;
	
	private PeerGroupProvider() {
		super( new Palaver());
	}
	
	public static PeerGroupProvider getInstance(){
		return attendee;
	}

	public PeerGroup getNetPeerGroup() {
		return netPeerGroup;
	}

	public void setNetPeerGroup(PeerGroup netPeerGroup) {
		this.netPeerGroup = netPeerGroup;
	}

	@Override
	protected void onDataReceived( String datum ) {
		if( datum.equals("PeerGroup"))
		  this.provide( this.netPeerGroup );
	}
}

class Palaver extends AbstractPalaver<String>{

	protected Palaver() {
		super("JXTA");
	}

	@Override
	public String giveToken() {
		return "peergroupToken";
	}

	@Override
	public boolean confirm(Object token) {
		return ( token instanceof String );
	}	
}