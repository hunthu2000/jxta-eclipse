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
package net.osgi.jp2p.chaupal.jxta.network.http;

import net.jp2p.container.component.AbstractJp2pService;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jxse.osgi.module.IJp2pServiceListener;
import net.jxta.platform.Module;
import net.osgi.jp2p.chaupal.Activator;
import net.osgi.jp2p.chaupal.jxta.IChaupalComponents.ChaupalComponents;

public class HttpService extends AbstractJp2pService<Module>{

	public HttpService( HttpServiceFactory factory ) {
		super(( IJp2pWritePropertySource<IJp2pProperties> ) factory.getPropertySource(), null );
	}

	@Override
	public void activate() {
		IJp2pServiceListener listener = Activator.getService();
		listener.requestService( ChaupalComponents.HTTP_SERVICE.toString() );
		super.activate();
	}

	@Override
	protected void deactivate() {
		IJp2pServiceListener listener = Activator.getService();
		listener.removeService( ChaupalComponents.HTTP_SERVICE.toString() );
	}
}