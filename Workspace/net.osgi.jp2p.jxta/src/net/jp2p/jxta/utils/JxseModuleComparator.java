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
package net.jp2p.jxta.utils;

import java.util.Comparator;

import net.jxta.platform.Module;
import net.jxta.compatibility.platform.NetworkConfigurator;
import net.jxta.compatibility.platform.NetworkManager;

public class JxseModuleComparator<T extends Object> implements
		Comparator<T> {

	@Override
	public int compare(T arg0, T arg1) {
		return getIndex( arg0 ) - getIndex( arg1 );
	}

	/**
	 * Create an index for the various modules
	 * @param obj
	 * @return
	 */
	public static int getIndex( Object obj  ){
		int index = 0;
		if( obj instanceof NetworkManager )
			return index;
		index++;
		if( obj instanceof NetworkConfigurator )
			return index;
		index++;
		if( obj instanceof Module )
			return index;
		return ++index;
	}
}
