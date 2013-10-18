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
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.registration.RegistrationPropertySource.RegistrationProperties;

public class RegistrationServiceFactory extends
		AbstractComponentFactory<RegistrationService, RegistrationProperties, IJxseDirectives> {

	public static final String S_DISCOVERY_SERVICE = "DiscoveryService";

	private IPeerGroupProvider peerGroupContainer;

	public RegistrationServiceFactory( IPeerGroupProvider peerGroupContainer, IJxsePropertySource<RegistrationProperties, IJxseDirectives> source ) {
		super( source );
		this.peerGroupContainer = peerGroupContainer;
	}

	@Override
	protected void onParseDirectivePriorToCreation( IJxseDirectives directive, Object value) {
	}

	@Override
	protected RegistrationService onCreateModule( IJxsePropertySource<RegistrationProperties, IJxseDirectives> properties) {
		RegistrationService service = new RegistrationService( peerGroupContainer, (IJxseWritePropertySource<RegistrationProperties, IJxseDirectives>) properties );
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