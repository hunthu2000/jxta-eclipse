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
package net.osgi.jxse.discovery;

import net.jxta.discovery.DiscoveryService;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class DiscoveryServiceFactory extends
		AbstractComponentFactory<DiscoveryService, IJxseProperties> {

	public static final String S_DISCOVERY_SERVICE = "DiscoveryService";

	private IPeerGroupProvider peerGroupContainer;

	public DiscoveryServiceFactory( IPeerGroupProvider peerGroupContainer, DiscoveryPropertySource source ) {
		super( source );
		this.peerGroupContainer = peerGroupContainer;
	}

	@Override
	protected DiscoveryService onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		if( peerGroupContainer.getPeerGroup() == null ){
			super.setCompleted(false );
			return null;
		}
			
		DiscoveryService service = peerGroupContainer.getPeerGroup().getDiscoveryService();
		return service;
	}
}