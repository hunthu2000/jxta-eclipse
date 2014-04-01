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
package net.jp2p.container.factory;

import net.jp2p.container.factory.AbstractComponentFactory;
import net.jp2p.container.factory.ComponentBuilderEvent;
import net.jp2p.container.factory.filter.IComponentFactoryFilter;

public abstract class AbstractComponentDependencyFactory<T extends Object, U extends Object> extends
		AbstractComponentFactory<T> {

	private IComponentFactoryFilter filter;
	private U dependency;

	public AbstractComponentDependencyFactory() {
		this.filter = this.createFilter();
	}

	/**
	 * Get the dependency that must be provided in order to allow creation of the component
	 * @return
	 */
	protected U getDependency() {
		return dependency;
	}

	/**
	 * Set the correrct filter
	 * @return
	 */
	protected abstract IComponentFactoryFilter createFilter();
	
	public IComponentFactoryFilter getFilter() {
		return filter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		if( filter != null ){
			if( filter.accept(event)){
				IComponentFactory<T> factory = (IComponentFactory<T>) event.getFactory();
				dependency = (U) factory.getComponent();
				setCanCreate( dependency != null );
				startComponent();
			}
		}
		super.notifyChange(event);
	}
}