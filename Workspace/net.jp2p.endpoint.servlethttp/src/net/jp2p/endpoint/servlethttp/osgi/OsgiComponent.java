/*******************************************************************************
 * Copyright (c) 2013 Condast.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Condast - initial API and implementation
 ******************************************************************************/
package net.jp2p.endpoint.servlethttp.osgi;

import org.eclipselabs.osgi.ds.broker.service.AbstractAttendeeProviderComponent;

/**
 * Makes the service container accessible for the IDE
 * @author keesp
 *
 */
public class OsgiComponent extends AbstractAttendeeProviderComponent {

	@Override
	public void initialise() {
		super.addAttendee( ServerStarterPetitioner.getInstance() );
	}

	@Override
	public void finalise() {
		super.finalise();
	}
}