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
package net.osgi.jxse.compatibility;

import org.eclipse.core.runtime.Platform;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.context.AbstractServiceContext;
import net.osgi.jxse.context.JxseContextPropertySource;

public abstract class AbstractExampleContext extends AbstractServiceContext{

	
	protected AbstractExampleContext( String bundle_id) {
		super(new JxseContextPropertySource( bundle_id ));
	}

	@Override
	public NetworkManager getModule(){
		return super.getNetworkManager();
	}

	@Override
	protected boolean onInitialising() {
		return true;
	}

	@Override
	protected void onFinalising() {
		this.getModule().stopNetwork();
	}

	/**
	 * Implement the 'main' method
	 * @param args
	 */
	protected abstract void main(String[] args);

	@Override
	public boolean start() {
		String[] args = Platform.getCommandLineArgs();
		main( args );
		return super.start();
	}

	
}
