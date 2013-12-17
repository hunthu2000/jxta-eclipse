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
package net.osgi.jxse.builder;

import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.utils.StringStyler;

public interface ICompositeBuilderListener<T extends Object> {

	public enum BuilderEvents{
		PROPERTY_SOURCE_CREATED,
		FACTORY_CREATED,
		COMPONENT_CREATED,
		COMPONENT_STARTED;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	
	public void notifyCreated( ComponentBuilderEvent<T> event );
}
