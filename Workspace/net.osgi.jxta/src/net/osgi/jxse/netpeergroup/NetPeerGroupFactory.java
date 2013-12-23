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
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.FactoryNode;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class NetPeerGroupFactory extends
		AbstractComponentFactory<NetPeerGroupService> implements ICompositeBuilderListener<FactoryNode<NetworkManager>>{

	public static final String S_DISCOVERY_SERVICE = "DiscoveryService";

	private NetworkManager manager;

	public NetPeerGroupFactory( NetPeerGroupPropertySource source, NetworkManager manager ) {
		super( source, false );
		this.manager = manager;
		super.setCanCreate(this.manager != null );
	}
	
	@Override
	protected NetPeerGroupService onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		NetPeerGroupService service = new NetPeerGroupService( this, manager );
		super.setCompleted( true );
		return service;
	}

	@Override
	public void notifyCreated(ComponentBuilderEvent<FactoryNode<NetworkManager>> event) {
		if( ! BuilderEvents.COMPONENT_CREATED.equals( event.getBuilderEvent()))
			return;
		//if( event.getComponent().getData() instanceof NetworkManagerFactory ){
		//	this.factory = (NetworkManagerFactory) event.getComponent().getData();
		//	super.setCanCreate(true);
		//}
	}
}