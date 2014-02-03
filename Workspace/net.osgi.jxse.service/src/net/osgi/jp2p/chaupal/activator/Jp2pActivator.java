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

import net.jp2p.container.IJp2pContainer;
import net.jp2p.container.activator.ISimpleActivator;
import net.jp2p.container.activator.Jp2pContainerStarter;

public class Jp2pActivator<T extends Object> implements ISimpleActivator {

	private IJp2pContainer<T> container;
	private Jp2pContainerStarter<IJp2pContainer<T>> starter;
	
	private boolean active;
		
	public Jp2pActivator() {
		active = false;
	}

	public void setJxtaContext( IJp2pContainer<T> jxtaContext) {
		this.container = jxtaContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public boolean start(){
		try{
			starter = new Jp2pContainerStarter<IJp2pContainer<T>>( container );
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

	public IJp2pContainer<?> getServiceContext(){
		return container;
	}

	@Override
	public boolean isActive() {
		return active;
	}
}