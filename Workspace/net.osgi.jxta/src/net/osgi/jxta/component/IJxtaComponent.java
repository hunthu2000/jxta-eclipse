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
package net.osgi.jxta.component;

import java.util.Date;
import java.util.Iterator;

import net.jxta.document.Advertisement;
import net.osgi.jxta.utils.StringStyler;

public interface IJxtaComponent<T extends Object>{
	
	public enum ModuleProperties{
		CREATE_DATE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}	
	}

	public Date getCreateDate();
	
	/**
	 * A JXTA service component can use, find or create a number of advertisements. This have to be listed
	 * @return
	 */
	public Advertisement[] getAdvertisements();
	
	/**
	 * Returns true if the component has advertisements
	 * @return
	 */
	public boolean hasAdvertisements();
	
	/**
	 * Get a property for the service component
	 * @param key
	 * @return
	 */
	public Object getProperty( Object key );

	/**
	 * Set a property for the service component
	 * @param key
	 * @return
	 */
	public void putProperty( Object key, Object value );

	/**
	 * Get the module that is contained in the component
	 * @return
	 */
	public T getModule();

	/**
	 * Get an iterator over the properties
	 * @return
	 */
	public Iterator<?> iterator();
}