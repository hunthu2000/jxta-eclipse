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
package net.osgi.jxse.service.advertisement;

import net.osgi.jxse.advertisement.JxseAdvertisementFactory;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class ChaupalAdvertisementFactory extends
		AbstractComponentFactory<JxseAdvertisementService>{

	public static final String S_DISCOVERY_SERVICE = "JxseDiscoveryService";

	private JxseAdvertisementFactory factory;
	
	public ChaupalAdvertisementFactory( JxseAdvertisementFactory factory ) {
		super( factory.getPropertySource() );
		this.factory = factory;
	}

	@Override
	protected JxseAdvertisementService onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		factory.createComponent();
		JxseAdvertisementService ds = new JxseAdvertisementService ( factory );
		return ds;
	}
}