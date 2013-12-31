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

public interface IJp2pWritePropertySource< T extends Object> extends IJp2pPropertySource<T> {
	
	public ManagedProperty<T,Object> getOrCreateManagedProperty(T id, Object value, boolean derived );

	/**
	 * Set the value for the given property
	 * @param id
	 * @param value
	 * @return 
	 */
	public boolean setProperty( T id, Object value );
	
	/**
	 * add a child source
	 * @param child
	 * @return
	 */
	public boolean addChild( IJp2pPropertySource<?> child );
	
	boolean setDirective(IJp2pDirectives id, String value); 
}
