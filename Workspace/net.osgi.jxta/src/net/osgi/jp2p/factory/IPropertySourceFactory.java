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
package net.osgi.jp2p.factory;

import net.osgi.jp2p.builder.ICompositeBuilderListener;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public interface IPropertySourceFactory<T extends Object> extends ICompositeBuilderListener<Object>{

	/**
	 * Get the component name that will be created
	 * @return
	 */
	public String getComponentName();
	
	/**
	 * Get the weight of the factory. By default, the context factory is zero, startup service is one
	 * @return
	 */
	public int getWeight();
	
	/**
	 * Returns true if the factory can create its product
	 * @return
	 */
	public boolean canCreate();

	/**
	 * Get the property source
	 * @return
	 */
	public IJp2pPropertySource<IJp2pProperties> getPropertySource();

	/**
	 * Get the property source that is used for the factor
	 * @return
	 */
	public IJp2pPropertySource<IJp2pProperties> createPropertySource();
	
	/**
	 * This method is called after the property sources have been created,
	 * to allow other factories to be added as well.
	 */
	public void extendContainer();
}