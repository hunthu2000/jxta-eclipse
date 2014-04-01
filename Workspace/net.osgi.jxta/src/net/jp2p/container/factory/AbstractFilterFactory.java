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

public abstract class AbstractFilterFactory<T extends Object> extends
		AbstractComponentFactory<T> {

	private IComponentFactoryFilter filter;

	public AbstractFilterFactory() {
		filter = this.createFilter();
	}

	/**
	 * Set the correrct filter
	 * @return
	 */
	protected abstract IComponentFactoryFilter createFilter();
	
	public IComponentFactoryFilter getFilter() {
		return filter;
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		if( filter != null ){
			if( filter.accept(event)){
				setCanCreate( true );
				startComponent();
			}
		}
		super.notifyChange(event);
	}
}