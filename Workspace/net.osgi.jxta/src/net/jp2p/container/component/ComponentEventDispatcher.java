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
package net.jp2p.container.component;

import java.util.ArrayList;
import java.util.Collection;

public class ComponentEventDispatcher {

	private Collection<IComponentChangedListener> listeners;

	private static ComponentEventDispatcher dispatcher = new ComponentEventDispatcher();
	
	private ComponentEventDispatcher() {
		this.listeners = new ArrayList<IComponentChangedListener>();
	}
	
	public static ComponentEventDispatcher getInstance(){
		return dispatcher;
	}

	public void addServiceChangeListener( IComponentChangedListener listener ){
		this.listeners.add( listener );
	}

	public void removeServiceChangeListener( IComponentChangedListener listener ){
		this.listeners.remove( listener );
	}
	
	public void serviceChanged( ComponentChangedEvent event ){
		for( IComponentChangedListener listener: this.listeners )
			listener.notifyServiceChanged(event);
	}

}
