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
import net.osgi.jxse.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;

public class DiscoveryServiceFactory extends
		AbstractComponentFactory<DiscoveryService, DiscoveryProperties, IJxseDirectives> {

	public static final String S_DISCOVERY_SERVICE = "DiscoveryService";

	private IPeerGroupProvider peerGroupContainer;

	public DiscoveryServiceFactory( IPeerGroupProvider peerGroupContainer, DiscoveryPropertySource source ) {
		super( source );
		this.peerGroupContainer = peerGroupContainer;
	}

	@Override
	protected void onParseDirectivePriorToCreation( IJxseDirectives directive, Object value) {
	}

	@Override
	protected DiscoveryService onCreateModule( IJxsePropertySource<DiscoveryProperties, IJxseDirectives> properties) {
		DiscoveryService service = peerGroupContainer.getPeerGroup().getDiscoveryService();
		return service;
	}
	

	@Override
	protected void onProperytySourceCreated(
			IJxsePropertySource<?, ?> ps) {
	}

	@Override
	protected void onParseDirectiveAfterCreation(IJxseDirectives directive,
			Object value) {
	}
}