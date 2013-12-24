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
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class DiscoveryServiceFactory extends
		AbstractComponentFactory<DiscoveryService> {

	public static final String S_DISCOVERY_SERVICE = "DiscoveryService";

	public DiscoveryServiceFactory( DiscoveryPropertySource source ) {
		super( source );
	}

	@Override
	protected DiscoveryService onCreateModule(
			IJxsePropertySource<IJxseProperties> properties) {
		// TODO Auto-generated method stub
		return null;
	}
}