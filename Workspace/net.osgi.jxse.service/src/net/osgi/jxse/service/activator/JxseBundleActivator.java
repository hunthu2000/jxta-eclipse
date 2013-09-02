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
package net.osgi.jxse.service.activator;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.context.IJxseServiceContext;
import net.osgi.jxse.service.xml.XMLServiceContext;

public class JxseBundleActivator extends AbstractJxseBundleActivator {

	String plugin_id;
	
	public JxseBundleActivator(String plugin_id) {
		this.plugin_id = plugin_id;
	}

	@Override
	protected IJxseServiceContext<NetworkManager> createContext() {
		return 	new XMLServiceContext( plugin_id, this.getClass() );
	}
}
