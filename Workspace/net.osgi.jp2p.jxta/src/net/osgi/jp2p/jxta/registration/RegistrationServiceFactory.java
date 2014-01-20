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
package net.osgi.jp2p.jxta.registration;

import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.osgi.jp2p.jxta.registration.RegistrationPropertySource;
import net.osgi.jp2p.jxta.registration.RegistrationService;

public class RegistrationServiceFactory extends
		AbstractComponentFactory<RegistrationService> {

	public RegistrationServiceFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent ) {
		super( container, parent );
	}

	@Override
	public String getComponentName() {
		return JxtaComponents.REGISTRATION_SERVICE.toString();
	}

	@Override
	protected RegistrationPropertySource onCreatePropertySource() {
		return new RegistrationPropertySource( super.getParentSource() );
	}

	@Override
	protected RegistrationService onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		RegistrationService service = new RegistrationService( (IJp2pWritePropertySource<IJp2pProperties>) properties );
		return service;
	}
}