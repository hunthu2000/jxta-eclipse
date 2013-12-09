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
package net.osgi.jxse.service.utils;

import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.context.IJxseServiceContext;

public class Utils
{

	/**
	 * Get the label of a JXTA service compoent
	 * @param component
	 * @return
	 */
	public static String getLabel( IJxseComponent<?,?> component) {
		if( component instanceof IJxseServiceContext ){
			IJxseServiceContext<?,?,?> container = (IJxseServiceContext<?,?,?> )component;
			return container.getIdentifier();			
		}
		if( component.getModule() == null )
			return component.getClass().getSimpleName();
		return component.getModule().getClass().getSimpleName();
	}
}
