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
package net.osgi.jxse.network.seed;

import net.osgi.jxse.properties.CategoryPropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;

public class SeedListPropertySource<T extends IJxseDirectives> extends CategoryPropertySource<T> {

	public static final String S_SEED_LIST = "Seed List";
	 
	String category, id;
	int depth;
	
	public SeedListPropertySource( IJxsePropertySource<String, T> source ) {
		super( S_SEED_LIST, source );
	}
}