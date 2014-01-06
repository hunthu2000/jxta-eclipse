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
package net.osgi.jp2p.component;

import java.util.EventObject;

import net.osgi.jp2p.container.AbstractServiceContainer.ServiceChange;

public class ComponentChangedEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	private ServiceChange change;
	
	public ComponentChangedEvent(Object arg0, ServiceChange change ) {
		super(arg0);
		this.change = change;
	}

	public ServiceChange getChange() {
		return change;
	}
}
