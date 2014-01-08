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
package net.osgi.jp2p.chaupal.discovery;

import net.jxta.discovery.DiscoveryService;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.jxta.discovery.DiscoveryServiceFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class ChaupalDiscoveryServiceFactory extends DiscoveryServiceFactory {

	@SuppressWarnings("unchecked")
	public ChaupalDiscoveryServiceFactory( ContainerBuilder container, DiscoveryServiceFactory factory ) {
		super( container, (IJp2pPropertySource<IJp2pProperties>) factory.getPropertySource().getParent() );
		super.setCanCreate(false);
		super.setSource(factory.getPropertySource());
	}

	@Override
	protected ChaupalDiscoveryService onCreateComponent( IJp2pPropertySource<IJp2pProperties> source) {
		IJp2pComponent<DiscoveryService> ds = super.onCreateComponent( source );
		ChaupalDiscoveryService service = new ChaupalDiscoveryService( (IJp2pWritePropertySource<IJp2pProperties>) source, ds.getModule() );
		return service;
	}

}