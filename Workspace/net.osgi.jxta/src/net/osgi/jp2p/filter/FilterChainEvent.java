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
package net.osgi.jp2p.filter;

import java.util.EventObject;

import net.osgi.jp2p.factory.IComponentFactory;

public class FilterChainEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	
	private IComponentFactory<?> factory;
	
	public FilterChainEvent(IComponentFactoryFilter filter, IComponentFactory<?> factory ){
		super(filter);
		this.factory = factory;
	}
	
	public IComponentFactoryFilter getFilter() {
		return (IComponentFactoryFilter) super.source;
	}

	/**
	 * Get the factory thAat caused the event
	 * @return
	 */
	public IComponentFactory<?> getFactory() {
		return factory;
	}
}
