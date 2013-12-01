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
import net.osgi.jxse.activator.ISimpleActivator;
import net.osgi.jxse.activator.JxseContextStarter;
import net.osgi.jxse.context.IJxseServiceContext;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;

public class JxseActivator implements ISimpleActivator {

	private IJxseServiceContext<NetworkManager, ContextProperties> jxtaContext;
	private JxseContextStarter<IJxseServiceContext<NetworkManager, ContextProperties>,NetworkManager, ContextProperties> starter;
	
	private boolean active;
		
	public JxseActivator() {
		active = false;
	}

	void setJxtaContext(IJxseServiceContext<NetworkManager, ContextProperties> jxtaContext) {
		this.jxtaContext = jxtaContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public boolean start(){
		try{
			starter = new JxseContextStarter<IJxseServiceContext<NetworkManager, ContextProperties>,NetworkManager, ContextProperties>( jxtaContext );
			starter.createContext();
			this.active = true;
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return this.active;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(){
		this.active = false;
		jxtaContext.stop();
		starter.removeContext();
	}

	public IJxseServiceContext<NetworkManager, ContextProperties> getServiceContext(){
		return jxtaContext;
	}

	@Override
	public boolean isActive() {
		return active;
	}
}