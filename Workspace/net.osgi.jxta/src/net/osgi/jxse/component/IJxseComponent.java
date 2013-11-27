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

import net.jxta.document.Advertisement;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.utils.StringStyler;

public interface IJxseComponent<T extends Object>{
	
	public enum ModuleProperties implements IJxseDirectives{
		ID,
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
}