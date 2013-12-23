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
package net.osgi.jxse.service.network;

import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.network.INetworkManagerProvider;
import net.osgi.jxse.network.NetworkManagerFactory;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class ChaupalNetworkManagerFactory extends
		AbstractComponentFactory<JxseNetworkManagerService> implements INetworkManagerProvider, IPeerGroupProvider{

	public static final String S_DISCOVERY_SERVICE = "JxseDiscoveryService";

	private NetworkManagerFactory factory;
	private IComponentFactory<?> parent;
	
	public ChaupalNetworkManagerFactory( IComponentFactory<?> parent, 
			NetworkManagerFactory factory ) {
		super( factory.getPropertySource() );
		this.factory = factory;
		this.parent = (IComponentFactory<?>) parent.getComponent();
	}

	@Override
	protected JxseNetworkManagerService onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		factory.createComponent();
		JxseNetworkManagerService ds = new JxseNetworkManagerService ( parent, factory );
		return ds;
	}

	@Override
	public String getPeerGroupName() {
		return this.factory.getComponent().getNetPeerGroup().getPeerGroupName();
	}

	@Override
	public PeerGroup getPeerGroup() {
		return this.factory.getComponent().getNetPeerGroup();
	}

	@Override
	public NetworkManager getNetworkManager() {
		return this.factory.getComponent();
	}
}