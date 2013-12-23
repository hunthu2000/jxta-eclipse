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
package net.osgi.jxse.component;

import java.util.Date;
import java.util.Iterator;

import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.utils.StringStyler;

public interface IJxseComponent<T extends Object, U extends Object>{
	
	public enum ModuleProperties implements IJxseProperties{
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
	public Iterator<U> iterator(); 
}