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

import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class RegistrationServiceFactory extends
		AbstractComponentFactory<RegistrationService> {

	public RegistrationServiceFactory( BuilderContainer container, IJxsePropertySource<IJxseProperties> parent ) {
		super( container, parent );
	}

	@Override
	public String getComponentName() {
		return Components.REGISTRATION_SERVICE.toString();
	}

	@Override
	protected RegistrationPropertySource onCreatePropertySource() {
		return new RegistrationPropertySource( super.getParentSource() );
	}

	@Override
	protected RegistrationService onCreateComponent( IJxsePropertySource<IJxseProperties> properties) {
		RegistrationService service = new RegistrationService( (IJxseWritePropertySource<IJxseProperties>) properties );
		return service;
	}
}