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
package net.osgi.jxse.factory;

import java.util.EventObject;

import net.osgi.jxse.builder.ICompositeBuilderListener.BuilderEvents;

public class ComponentBuilderEvent<T extends Object> extends EventObject {
	private static final long serialVersionUID = 1L;

	private BuilderEvents builderEvent;
	
	private T component;
	
	public ComponentBuilderEvent(Object arg0, T component, BuilderEvents factoryEvent ){
		super(arg0);
		this.component = component;
		this.builderEvent = factoryEvent;
	}
	
	public T getComponent() {
		return component;
	}

	public BuilderEvents getBuilderEvent() {
		return builderEvent;
	}
}
