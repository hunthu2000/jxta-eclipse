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
import net.osgi.jxse.builder.IJxseModule;

public class ComponentBuilderEvent<T extends Object> extends EventObject {
	private static final long serialVersionUID = 1L;

	private BuilderEvents builderEvent;
	
	private IJxseModule<T> module;
	
	public ComponentBuilderEvent(Object arg0, IJxseModule<T> module, BuilderEvents factoryEvent ){
		super(arg0);
		this.module = module;
		this.builderEvent = factoryEvent;
	}
	
	public IJxseModule<T> getModule() {
		return module;
	}

	public BuilderEvents getBuilderEvent() {
		return builderEvent;
	}
}
