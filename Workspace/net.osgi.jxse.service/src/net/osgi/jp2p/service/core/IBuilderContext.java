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
package net.osgi.jp2p.service.core;

import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.service.IContextObserver;

public interface IBuilderContext<T extends Object,U extends IJp2pProperties> {

	public abstract IContextObserver<T> getObserver();

	public abstract void setObserver(IContextObserver<T> observer);

}