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
package net.jp2p.container.factory;

public interface IComponentFactory<T extends Object> extends IPropertySourceFactory<T>{
	
	/**
	 * Some services need to start prior to the creation of properties. This can be performed here.
	 */
	public void earlyStart();
	
	/**
	 * The completion is not necessarily the same as creating the module. This method has to 
	 * be called separately;
	 * @return
	 */
	public boolean complete();
	
	public boolean isCompleted();
	
	public boolean hasFailed();
	
	/**
	 * Get the service component. Returns null if the factory was not completed
	 * @return
	 */
	public T getComponent();
}