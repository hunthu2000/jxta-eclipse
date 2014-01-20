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
package net.osgi.jp2p.chaupal.activator;

import net.osgi.jp2p.activator.ISimpleActivator;
import net.osgi.jp2p.activator.Jp2pContextStarter;
import net.osgi.jp2p.container.Jp2pServiceContainer;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pProperties;

public class Jp2pActivator implements ISimpleActivator {

	private Jp2pServiceContainer container;
	private Jp2pContextStarter<Jp2pServiceContainer, IJp2pProperties, IJp2pDirectives> starter;
	
	private boolean active;
		
	public Jp2pActivator() {
		active = false;
	}

	void setJxtaContext(Jp2pServiceContainer jxtaContext) {
		this.container = jxtaContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public boolean start(){
		try{
			starter = new Jp2pContextStarter<Jp2pServiceContainer, IJp2pProperties, IJp2pDirectives>( container );
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
		container.stop();
		starter.removeContext();
	}

	public Jp2pServiceContainer getServiceContext(){
		return container;
	}

	@Override
	public boolean isActive() {
		return active;
	}
}