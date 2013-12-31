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
package net.osgi.jp2p.discovery;

import net.jxta.discovery.DiscoveryService;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.component.Jp2pComponent;
import net.osgi.jp2p.factory.AbstractPeerGroupDependencyFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public class DiscoveryServiceFactory extends
		AbstractPeerGroupDependencyFactory<DiscoveryService> {

	public DiscoveryServiceFactory( ContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent) {
		super( container, parent );
	}

	@Override
	public String getComponentName() {
		return Components.DISCOVERY_SERVICE.toString();
	}

	@Override
	protected DiscoveryPropertySource onCreatePropertySource() {
		return new DiscoveryPropertySource( super.getParentSource() );
	}

	@Override
	protected IJp2pComponent<DiscoveryService> onCreateComponent(
			IJp2pPropertySource<IJp2pProperties> properties) {
		return new Jp2pComponent<DiscoveryService>( super.getPeerGroup().getDiscoveryService());
	}
}