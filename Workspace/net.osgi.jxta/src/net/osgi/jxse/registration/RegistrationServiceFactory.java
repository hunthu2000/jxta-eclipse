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
package net.osgi.jxse.registration;

import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class RegistrationServiceFactory extends
		AbstractComponentFactory<RegistrationService> {

	public static final String S_DISCOVERY_SERVICE = "DiscoveryService";

	public RegistrationServiceFactory( IJxsePropertySource<IJxseProperties> source ) {
		super( source );
	}

	@Override
	protected void onParseDirectivePriorToCreation( IJxseDirectives directive, Object value) {
	}

	@Override
	protected RegistrationService onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		RegistrationService service = new RegistrationService( (IJxseWritePropertySource<IJxseProperties>) properties );
		return service;
	}
}