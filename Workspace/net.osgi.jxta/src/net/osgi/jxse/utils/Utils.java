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
package net.osgi.jxse.utils;

import java.util.Arrays;
import java.util.Collection;

public class Utils
{

	/**
	 * returns true if the string is null or empty
	 * @param str
	 * @return
	 */
	public static final boolean isNull( String str ){
		return (( str == null) || ( str.trim().length() == 0 ));
	}
	
	/**
	 * add the given array to the collection
	 * @param col
	 * @param add
	 */
	public static void addArray( Collection<Object> col, Object[] add ){
		if(( add == null ) || ( add.length == 0 ))
			return;
		col.addAll( Arrays.asList( add ));
	}
	
}
