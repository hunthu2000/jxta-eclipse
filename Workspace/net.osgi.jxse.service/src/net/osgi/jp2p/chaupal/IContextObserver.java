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

import net.osgi.jp2p.container.IJxseServiceContainer;
import net.osgi.jp2p.factory.IComponentFactory;

public interface IContextObserver<T extends Object> {

	public void buildStarted( IJxseServiceContainer<T> context );
	
	/**
	 * Observes the creation of a factory in a context 
	 * @param factory
	 */
	public void factoryCreated( IComponentFactory<?> factory );

	/**
	 * Observes the creation of a component in a context 
	 * @param factory
	 */
	public void componentCreated( Object component );

	public void buildCompleted( IJxseServiceContainer<T> context );

}
