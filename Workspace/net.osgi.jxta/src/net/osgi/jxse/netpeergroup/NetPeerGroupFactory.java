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
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.network.NetworkManagerFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class NetPeerGroupFactory extends
		AbstractComponentFactory<NetPeerGroupService, IJxseProperties> implements ICompositeBuilderListener<ComponentNode<NetworkManager,IJxseProperties>>{

	public static final String S_DISCOVERY_SERVICE = "DiscoveryService";

	private NetworkManagerFactory factory;

	public NetPeerGroupFactory( NetPeerGroupPropertySource source ) {
		super( source, false );
		this.factory = null;
	}
	
	@Override
	protected NetPeerGroupService onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		if( factory.getPeerGroup() == null ){
			super.setCompleted(false );
			return null;
		}			
		return new NetPeerGroupService( factory );
	}

	@Override
	public void notifyCreated(ComponentBuilderEvent<ComponentNode<NetworkManager,IJxseProperties>> event) {
		if( ! BuilderEvents.COMPONENT_CREATED.equals( event.getBuilderEvent()))
			return;
		if( event.getComponent().getFactory() instanceof NetworkManagerFactory ){
			this.factory = (NetworkManagerFactory) event.getComponent().getFactory();
			super.setCanCreate(true);
		}
	}
}