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
package net.osgi.jp2p.chaupal;

import java.util.ArrayList;
import java.util.Collection;

public class ServiceEventDispatcher {

	private Collection<IServiceChangedListener> listeners;

	private static ServiceEventDispatcher dispatcher = new ServiceEventDispatcher();
	
	private ServiceEventDispatcher() {
		this.listeners = new ArrayList<IServiceChangedListener>();
	}
	
	public static ServiceEventDispatcher getInstance(){
		return dispatcher;
	}

	public void addServiceChangeListener( IServiceChangedListener listener ){
		this.listeners.add( listener );
	}

	public void removeServiceChangeListener( IServiceChangedListener listener ){
		this.listeners.remove( listener );
	}
	
	public void serviceChanged( ServiceChangedEvent event ){
		for( IServiceChangedListener listener: this.listeners )
			listener.notifyServiceChanged(event);
	}

}
