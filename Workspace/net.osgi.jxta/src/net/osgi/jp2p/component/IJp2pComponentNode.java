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
package net.osgi.jp2p.component;

import java.util.Collection;

public interface IJp2pComponentNode<T extends Object> extends IJp2pComponent<T>{
	
	/**
	 * Return true if the component is a root
	 * @return
	 */
	public boolean isRoot();
	
	public void addChild( IJp2pComponent<?> child );
	public void removeChild( IJp2pComponent<?> child );

	/**
	 * Get the parent of the component
	 * @return
	 */
	public Collection<IJp2pComponent<?>> getChildren();
	
	public boolean hasChildren();
}