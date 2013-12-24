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

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class NetPeerGroupFactory extends AbstractComponentFactory<NetPeerGroupService>{

	private NetworkManager manager;

	public NetPeerGroupFactory( NetPeerGroupPropertySource source, NetworkManager manager ) {
		super( source, false );
		this.manager = manager;
		super.setCanCreate(this.manager != null );
	}
	
	
	@Override
	public boolean canCreate() {
		return ( this.manager != null );
	}


	@Override
	protected NetPeerGroupService onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		NetPeerGroupService service = new NetPeerGroupService( this, manager );
		super.setCompleted( true );
		return service;
	}
}