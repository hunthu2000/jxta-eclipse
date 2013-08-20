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

public class ServiceComponentEvent<T extends Object> extends EventObject {

	private static final long serialVersionUID = 302931451825865288L;

	private T module;
	
	public ServiceComponentEvent( IComponentFactory<T> source, T module ) {
		super(source);
		this.module = module;
	}

	public T getModule(){
		return module;
	}
}