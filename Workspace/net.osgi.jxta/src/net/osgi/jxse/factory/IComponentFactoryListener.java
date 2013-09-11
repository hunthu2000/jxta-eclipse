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
package net.osgi.jxse.factory;

import net.osgi.jxse.properties.IJxseDirectives;

public interface IComponentFactoryListener<T extends Object, U extends Enum<U>, V extends IJxseDirectives> {
	public void notifyComponentCompleted( JxseComponentEvent<T,U,V> event );
}
