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
package net.osgi.jxse.service.core;

import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.service.IContextObserver;

public interface IBuilderContext<T extends Object,U extends IJxseProperties, V extends IJxseDirectives> {

	public abstract IContextObserver<T,U,V> getObserver();

	public abstract void setObserver(IContextObserver<T,U,V> observer);

}