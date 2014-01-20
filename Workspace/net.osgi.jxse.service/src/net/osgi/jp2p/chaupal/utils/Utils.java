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
package net.osgi.jp2p.chaupal.utils;

import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.container.IJxseServiceContainer;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public class Utils
{

	/**
	 * Get the label of a JP2P service compoent
	 * @param component
	 * @return
	 */
	public static String getLabel( IJp2pComponent<?> component) {
		if( component instanceof IJxseServiceContainer ){
			IJxseServiceContainer<?> container = (IJxseServiceContainer<?> )component;
			return container.getIdentifier();			
		}
		if( component.getModule() == null )
			return component.getClass().getSimpleName();
		return component.getModule().getClass().getSimpleName();
	}

	/**
	 * Get the label of a JP2P service compoent
	 * @param component
	 * @return
	 */
	public static String getLabel( IJp2pPropertySource<?> source ) {
		String label = source.getDirective( Directives.NAME );
		if(( label == null ) || ( label.length() == 0 )){
			return source.getComponentName();			
		}
		return label;
	}
}
