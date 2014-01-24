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

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.factory.AbstractComponentFactory;
import net.jp2p.container.factory.ComponentBuilderEvent;
import net.jp2p.container.factory.IComponentFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;

public abstract class AbstractDependencyFactory<T extends Object, U extends Object> extends
		AbstractComponentFactory<T> {

	private U dependency;

	public AbstractDependencyFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent ) {
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
			IComponentFactory<?> factory = (IComponentFactory<?>) event.getFactory();
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