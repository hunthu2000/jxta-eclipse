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
package net.jp2p.chaupal.utils;

import net.jp2p.container.IJp2pContainer;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pDirectives.Directives;

public class Utils
{

	/**
	 * Get the label of a JP2P service component
	 * @param component
	 * @return
	 */
	public static String getLabel( IJp2pComponent<?> component) {
		if( component instanceof IJp2pContainer ){
			IJp2pContainer container = (IJp2pContainer )component;
			return container.getIdentifier();			
		}
		if(( component == null ) || ( component.getPropertySource() == null ))
			return "NULL";
		if( !net.jp2p.container.utils.Utils.isNull( component.getComponentLabel() ))
			return component.getComponentLabel();
		if( component.getModule() == null )
			return component.getClass().getSimpleName();
		return component.getModule().getClass().getSimpleName();
	}

	/**
	 * Get the label of a JP2P service component
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
