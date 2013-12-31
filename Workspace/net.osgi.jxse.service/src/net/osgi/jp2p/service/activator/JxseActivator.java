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
package net.osgi.jp2p.service.activator;

import net.jxta.platform.NetworkManager;
import net.osgi.jp2p.activator.ISimpleActivator;
import net.osgi.jp2p.activator.Jp2pContextStarter;
import net.osgi.jp2p.context.Jp2pServiceContainer;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pProperties;

public class JxseActivator implements ISimpleActivator {

	private Jp2pServiceContainer jxtaContext;
	private Jp2pContextStarter<Jp2pServiceContainer, NetworkManager, IJp2pProperties, IJp2pDirectives> starter;
	
	private boolean active;
		
	public JxseActivator() {
		active = false;
	}

	void setJxtaContext(Jp2pServiceContainer jxtaContext) {
		this.jxtaContext = jxtaContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public boolean start(){
		try{
			starter = new Jp2pContextStarter<Jp2pServiceContainer,NetworkManager, IJp2pProperties, IJp2pDirectives>( jxtaContext );
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

	public Jp2pServiceContainer getServiceContext(){
		return jxtaContext;
	}

	@Override
	public boolean isActive() {
		return active;
	}
}