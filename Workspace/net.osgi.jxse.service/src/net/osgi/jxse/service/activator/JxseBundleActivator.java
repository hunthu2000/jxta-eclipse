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
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.context.IJxseServiceContext;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.service.xml.XMLServiceContext;

public class JxseBundleActivator extends AbstractJxseBundleActivator {

	private String bundle_id;
	private ICompositeBuilderListener observer;
	
	
	public JxseBundleActivator(String bundle_id) {
		this.bundle_id = bundle_id;
	}

	public ICompositeBuilderListener getObserver() {
		return observer;
	}

	public void setObserver(ICompositeBuilderListener observer) {
		this.observer = observer;
	}

	//@Override
	protected IJxseServiceContext<NetworkManager, ContextProperties> createContext() {
		XMLServiceContext context = new XMLServiceContext( bundle_id, this.getClass() );
		context.setObserver(observer);
		context.initialise();
		return context;
	}
}
