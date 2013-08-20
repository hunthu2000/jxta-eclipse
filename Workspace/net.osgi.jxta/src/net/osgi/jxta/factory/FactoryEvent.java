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
package net.osgi.jxta.factory;

import java.util.EventObject;

import net.osgi.jxta.factory.ICompositeFactoryListener.FactoryEvents;

public class FactoryEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	private FactoryEvents factoryEvent;
	
	private IComponentFactory<?> factory;
	
	public FactoryEvent(Object arg0, IComponentFactory<?> factory, FactoryEvents factoryEvent ){
		super(arg0);
		this.factory = factory;
		this.factoryEvent = factoryEvent;
	}
	
	public IComponentFactory<?> getFactory() {
		return factory;
	}

	public FactoryEvents getFactoryEvent() {
		return factoryEvent;
	}
}
