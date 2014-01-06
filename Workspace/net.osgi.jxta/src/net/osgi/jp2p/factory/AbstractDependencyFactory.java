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

import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public abstract class AbstractDependencyFactory<T extends Object, U extends Object> extends
		AbstractComponentFactory<T> {

	private U dependency;

	public AbstractDependencyFactory( ContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent ) {
		super( container, parent );
	}

	/**
	 * Get the dependency that must be provided in order to allow creation of the cpomponent
	 * @return
	 */
	protected U getDependency() {
		return dependency;
	}

	/**
	 * Get the component name of the dependency
	 * @return
	 */
	protected abstract boolean isCorrectFactory( IComponentFactory<?> factory );
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		switch( event.getBuilderEvent()){
		case COMPONENT_CREATED:
			IComponentFactory<?> factory = event.getFactory();
			if( !this.isCorrectFactory( factory ))
				return;
			dependency = (U) factory.getComponent();
			super.setCanCreate( dependency != null );
			super.startComponent();
			break;
		default:
			break;
		}
		super.notifyChange(event);
	}

}