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
package net.jp2p.container.activator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.jp2p.container.Jp2pServiceContainer;
import net.jp2p.container.properties.IJp2pDirectives;
import net.jp2p.container.properties.IJp2pProperties;

/**
 * Starts the JXTA service container
 * @author keesp
 *
 */
public class Jp2pContextStarter<T extends Jp2pServiceContainer, V extends IJp2pProperties, W extends IJp2pDirectives> implements Runnable{

	private T activator;
	
	private ExecutorService executor;

	
	public Jp2pContextStarter( T activator ) {
		super();
		this.activator = activator;
		executor = Executors.newSingleThreadExecutor();
	}

	public T createContext(){
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
		System.err.println( "Container " + activator.getIdentifier() + " started successfully" );
	}
}