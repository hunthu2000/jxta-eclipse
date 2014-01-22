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
package net.jp2p.container.component;

import java.util.Date;
import java.util.Iterator;

import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.utils.StringStyler;

public interface IJp2pComponent<T extends Object>{
	
	public enum ModuleProperties implements IJp2pProperties{
		CREATE_DATE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}	
	}

	/**
	 * Get the id of the component
	 * @return
	 */
	public String getId();
	
	public Date getCreateDate();
	
	/**
	 * Get the property source of this component
	 * @return
	 */
	public IJp2pPropertySource<IJp2pProperties> getPropertySource();
	
	/**
	 * Get a property for the service component
	 * @param key
	 * @return
	 */
	public Object getProperty( Object key );

	/**
	 * Get the category for the given key
	 * @param key
	 * @return
	 */
	public String getCategory( Object key );

	/**
	 * Get the module that is contained in the component
	 * @return
	 */
	public T getModule();
	
	/**
	 * Get an iterator for the supported properties
	 * @return
	 */
	public Iterator<IJp2pProperties> iterator(); 
}