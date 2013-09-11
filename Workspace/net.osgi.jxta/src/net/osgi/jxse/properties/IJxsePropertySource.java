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
package net.osgi.jxse.properties;

import java.util.Iterator;

public interface IJxsePropertySource< T extends Enum<T>, U extends IJxseDirectives> {

	public static final String JXTA_SETTINGS = "jxta.settings";
	public static final String S_USER_HOME = "user.home";
	public static final String S_JXTA = ".jxta";

	/**
	 * Get the plugin ID
	 * @return
	 */
	public String getBundleId();

	/**
	 * Get the identifier
	 * @return
	 */
	public String getIdentifier();

	/**
	 * Get the component name
	 * @return
	 */
	public String getComponentName();

	/**
	 * Get the id of the component that this property source is bound to
	 * @return
	 */
	public String getId();
	
	/**
	 * Get the depth of the component (root = 0)
	 * @return
	 */
	public int getDepth();
	
	/**
	 * Get the default value for the property
	 * @param id
	 * @return
	 */
	public Object getDefault( T id );
	
	/**
	 * Get the current value for the given property
	 */
	public Object getProperty( T id );
	
	/**
	 * Set the value for the given property
	 * @param id
	 * @param value
	 * @return 
	 */
	public boolean setProperty( T id, Object value );
	
	/**
	 * Get an iterator over the properties
	 * @return
	 */
	public Iterator<T> propertyIterator();
	
	/**
	 * Validate the given property
	 * @param id
	 * @return
	 */
	public boolean validate( T id, Object value );

	/**
	 * Get the default directives
	 * @param id
	 * @return
	 */
	public Object getDirective( U id );

	/**
	 * Get the default directives
	 * @param id
	 * @return
	 */
	public Object getDefaultDirectives( U id);

	/**
	 * Get an iterator over the directives
	 * @return
	 */
	public Iterator<U> directiveIterator();

	/**
	 * Get the children of the property source
	 * @return
	 */
	public IJxsePropertySource<?,?>[] getChildren();

	boolean setDirective(U id, Object value); 
}
