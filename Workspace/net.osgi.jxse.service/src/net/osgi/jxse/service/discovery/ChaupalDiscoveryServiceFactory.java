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
import net.osgi.jxse.discovery.DiscoveryServiceFactory;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class ChaupalDiscoveryServiceFactory extends
		AbstractComponentFactory<JxseDiscoveryService> {

	public static final String S_DISCOVERY_SERVICE = "JxseDiscoveryService";

	private DiscoveryServiceFactory factory;

	public ChaupalDiscoveryServiceFactory( DiscoveryServiceFactory factory ) {
		super( factory.getPropertySource() );
		this.factory = factory;
	}

	@Override
	protected void onParseDirectivePriorToCreation( IJxseDirectives directive, Object value) {
		
	}

	@Override
	protected JxseDiscoveryService onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		DiscoveryService ds = factory.createComponent();
		if( ds == null )
			return null;
		JxseDiscoveryService service = new JxseDiscoveryService( factory );
		return service;
	}
}