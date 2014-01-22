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
package net.jp2p.container.builder;

import net.jp2p.container.factory.ComponentBuilderEvent;
import net.jp2p.container.utils.StringStyler;

public interface ICompositeBuilderListener<T extends Object> {

	public enum BuilderEvents{
		PROPERTY_SOURCE_CREATED,
		COMPONENT_PREPARED,
		COMPONENT_CREATED,
		COMPONENT_STARTED,
		FACTORY_COMPLETED,
		FACTORY_ADDED_TO_CONTAINER,
		FACORY_REMOVED_FROM_CONTAINER;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	
	public void notifyChange( ComponentBuilderEvent<T> event );
}
