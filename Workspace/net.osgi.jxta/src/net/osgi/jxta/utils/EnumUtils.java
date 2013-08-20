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
package net.osgi.jxta.utils;

public class EnumUtils {

	/**
	 * Return a String representation of the given objects. Is used to
	 * quickly modify enums
	 * @param objects
	 * @return
	 */
	public static String[] toString( Object[] objects ){
		String[] results = new String[objects.length ];
		for( int i=0; i<objects.length; i++ )
			results[i] = objects[i].toString();
		return results;
	}
	
	//public String[] getValuesAsString( Enum enm ){
	//	for( )
	//}

}
