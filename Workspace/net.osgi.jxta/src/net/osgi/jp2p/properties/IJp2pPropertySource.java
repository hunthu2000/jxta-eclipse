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
package net.osgi.jp2p.properties;

import java.util.Iterator;

public interface IJp2pPropertySource< T extends Object> {

	public static final String JP2P_SETTINGS = "jp2p.settings";
	public static final String S_USER_HOME = "user.home";
	public static final String S_JP2P = ".jp2p";

	/**
	 * Returns the parent, or null of the property source is a root
	 * @return
	 */
	public IJp2pPropertySource<?> getParent();
	
	/**
	 * Get the id from a string representation
	 * @param key
	 * @return
	 */
	public T getIdFromString( String key );
	
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
	 * Get the category for the given key
	 * @param key
	 * @return
	 */
	public String getCategory( T id );

	public ManagedProperty<T,Object> getManagedProperty( T id );
	
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
	public String getDirective( IJp2pDirectives id );

	/**
	 * Get an iterator over the directives
	 * @return
	 */
	public Iterator<IJp2pDirectives> directiveIterator();

	/**
	 * add a child to the property source
	 * @param child
	 * @return
	 */
	public boolean addChild( IJp2pPropertySource<?> child );

	/**
	 * Remove a child from the property source
	 * @param child
	 */
	public void removeChild( IJp2pPropertySource<?> child );

	/**
	 * Get the children of the property source
	 * @return
	 */
	public IJp2pPropertySource<?>[] getChildren();
	
	/**
	 * Get the child with the given component name
	 * @param componentName
	 * @return
	 */
	public IJp2pPropertySource<?> getChild( String componentName );

	public boolean isEmpty();
}
