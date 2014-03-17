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
package net.jp2p.chaupal.activator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Jp2pActivator<T extends Object> implements Runnable {

	private AbstractJp2pBundleActivator<T> activator;
	
	private ExecutorService executor;
		
	public Jp2pActivator( AbstractJp2pBundleActivator<T> activator ) {
		this.activator = activator;
		executor = Executors.newSingleThreadExecutor();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(){
		executor.execute(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(){
		executor.shutdown();
	}

	@Override
	public void run() {
		try{
			activator.createContainer();
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}
}