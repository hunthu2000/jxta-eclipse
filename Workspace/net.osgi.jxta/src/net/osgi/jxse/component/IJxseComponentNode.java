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

import java.util.Collection;

import net.osgi.jxse.properties.IJxseProperties;

public interface IJxseComponentNode<T extends Object> extends IJxseComponent<T,IJxseProperties>{
	
	/**
	 * Return true if the component is a root
	 * @return
	 */
	public boolean isRoot();
	
	/**
	 * Get the parent of the component
	 * @return
	 */
	public IJxseComponent<?,?> getParent();

	public void addChild( IJxseComponent<?,?> child );
	public void removeChild( IJxseComponent<?,?> child );

	/**
	 * Get the parent of the component
	 * @return
	 */
	public Collection<IJxseComponent<?,?>> getChildren();
	
	public boolean hasChildren();
}