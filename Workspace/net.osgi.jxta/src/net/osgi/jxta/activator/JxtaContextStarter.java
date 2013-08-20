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
package net.osgi.jxta.activator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Starts the JXTA service container
 * @author keesp
 *
 */
public class JxtaContextStarter<T extends AbstractActivator<U>, U extends Object> implements Runnable{

	private T activator;
	
	private ExecutorService executor;

	
	public JxtaContextStarter( T activator ) {
		super();
		this.activator = activator;
		executor = Executors.newSingleThreadExecutor();
	}

	public T createContext(){

		activator.initialise();
		executor.execute(this);
		return activator;
	}

	public void removeContext(){
		executor.shutdown();
		this.activator.stop();
		this.activator = null;
	}

	@Override
	public void run() {
		this.activator.start();
		System.err.println( "Container started successfully");
	}
}