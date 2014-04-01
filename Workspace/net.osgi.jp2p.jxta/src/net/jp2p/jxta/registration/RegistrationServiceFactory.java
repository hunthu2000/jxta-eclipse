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
package net.jp2p.jxta.registration;

import net.jp2p.container.factory.AbstractComponentFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.jp2p.jxta.registration.RegistrationPropertySource;
import net.jp2p.jxta.registration.RegistrationService;

public class RegistrationServiceFactory extends
		AbstractComponentFactory<RegistrationService> {

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