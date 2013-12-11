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
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.network.NetworkManagerFactory;
import net.osgi.jxse.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class ChaupalNetworkManagerFactory extends
		AbstractComponentFactory<JxseNetworkManagerService, NetworkManagerProperties, IJxseDirectives> implements IPeerGroupProvider{

	public static final String S_DISCOVERY_SERVICE = "JxseDiscoveryService";

	private NetworkManagerFactory factory;
	private IComponentFactory<?, IJxseProperties, IJxseDirectives> parent;
	
	@SuppressWarnings("unchecked")
	public ChaupalNetworkManagerFactory( IComponentFactory<?, IJxseProperties, IJxseDirectives> parent, 
			NetworkManagerFactory factory ) {
		super( factory.getPropertySource() );
		this.factory = factory;
		this.parent = (IComponentFactory<?, IJxseProperties, IJxseDirectives>) parent.getModule();
	}

	@Override
	protected JxseNetworkManagerService onCreateModule( IJxsePropertySource<NetworkManagerProperties, IJxseDirectives> properties) {
		factory.createModule();
		JxseNetworkManagerService ds = new JxseNetworkManagerService ( parent, factory );
		return ds;
	}

	@Override
	public String getPeerGroupName() {
		return this.factory.getModule().getNetPeerGroup().getPeerGroupName();
	}

	@Override
	public PeerGroup getPeerGroup() {
		return this.factory.getModule().getNetPeerGroup();
	}
}