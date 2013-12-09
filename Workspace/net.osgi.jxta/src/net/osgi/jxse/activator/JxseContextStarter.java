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
package net.osgi.jxse.activator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.osgi.jxse.context.JxseServiceContext;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;

/**
 * Starts the JXTA service container
 * @author keesp
 *
 */
public class JxseContextStarter<T extends JxseServiceContext, U extends Object, V extends IJxseProperties, W extends IJxseDirectives> implements Runnable{

	private T activator;
	
	private ExecutorService executor;

	
	public JxseContextStarter( T activator ) {
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