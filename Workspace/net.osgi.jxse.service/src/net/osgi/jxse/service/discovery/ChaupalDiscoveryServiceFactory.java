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
package net.osgi.jxse.service.discovery;

import net.jxta.discovery.DiscoveryService;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.discovery.DiscoveryServiceFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class ChaupalDiscoveryServiceFactory extends DiscoveryServiceFactory {

	@SuppressWarnings("unchecked")
	public ChaupalDiscoveryServiceFactory( BuilderContainer container, DiscoveryServiceFactory factory ) {
		super( container, (IJxsePropertySource<IJxseProperties>) factory.getPropertySource().getParent() );
		super.setSource(factory.createPropertySource());
	}

	@Override
	protected ChaupalDiscoveryService onCreateComponent( IJxsePropertySource<IJxseProperties> source) {
		IJxseComponent<DiscoveryService> ds = super.onCreateComponent( source );
		ChaupalDiscoveryService service = new ChaupalDiscoveryService( (IJxseWritePropertySource<IJxseProperties>) source, ds.getModule() );
		return service;
	}
}