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
package net.osgi.jp2p.factory;

import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.filter.IComponentFactoryFilter;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public abstract class AbstractComponentDependencyFactory<T extends Object, U extends Object> extends
		AbstractComponentFactory<T> {

	private IComponentFactoryFilter filter;
	private U dependency;

	public AbstractComponentDependencyFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent ) {
		super( container, parent );
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
			if( filter.accept((ComponentBuilderEvent<T>) event)){
				IComponentFactory<T> factory = (IComponentFactory<T>) event.getFactory();
				dependency = (U) factory.getComponent();
				setCanCreate( dependency != null );
				startComponent();
			}
		}
		super.notifyChange(event);
	}
}