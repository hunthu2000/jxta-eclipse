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

import java.util.Collection;

public interface IJxtaComponentNode<T extends Object> extends IJxtaComponent<T>{
	
	/**
	 * Return true if the component is a root
	 * @return
	 */
	public boolean isRoot();
	
	/**
	 * Get the parent of the component
	 * @return
	 */
	public IJxtaComponent<?> getParent();

	public void addChild( IJxtaComponent<?> child );
	public void removeChild( IJxtaComponent<?> child );

	/**
	 * Get the parent of the component
	 * @return
	 */
	public Collection<IJxtaComponent<?>> getChildren();
	
	public boolean hasChildren();
}