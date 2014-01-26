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
package net.jp2p.container.utils;

public interface INode<T,U extends Object>{
	
	/**
	 * Return true if the component is a root
	 * @return
	 */
	public boolean isRoot();
	
	public boolean addChild( U child );
	public void removeChild( U child );

	/**
	 * Get the parent of the component
	 * @return
	 */
	public U[] getChildren();
	
	public boolean hasChildren();
}