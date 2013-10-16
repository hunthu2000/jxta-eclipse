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
package net.osgi.jxse.properties;

import net.osgi.jxse.utils.Utils;

public class CategoryPropertySource<T extends IJxseDirectives> extends AbstractJxseWritePropertySource<String, T> implements IJxseWritePropertySource<String,T> {

	public static final String S_DOT_REGEX = "[.]";
	
	String category, id;
	
	public CategoryPropertySource( String cat, IJxsePropertySource<String, T> source ) {
		super( cat, source );
		String[] split = cat.split("[.]");
		this.category = split[0];
		this.id = source.getId() + "." + category;
	}

	@Override
	public String getIdFromString(String key) {
		return key;
	}


	public String getId() {
		return id;
	}

	@Override
	public String getComponentName() {
		return category;
	}
	
	@Override
	public boolean addChild(IJxsePropertySource<?, ?> child) {
		return super.addChild(child);
	}

	@Override
	public boolean setDirective(T id, Object value) {
		return super.setDirective(id, value);
	}

	/**
	 * Returns the category, by looking for '_8' in the id. Returns null if
	 * no category was found 
	 * @param id
	 * @return
	 */
	public static String getCategory( String id ){
		String cat = id.toLowerCase().replace("_8", ".");
		String[] split = cat.split("[.]");
		if( split.length <= 1 )
			return null;
		else
			cat.replace(split[ split.length - 1], "");
			return cat.trim();
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IJxsePropertySource<?, ?> createCategoryPropertySource( String category,  IJxseWritePropertySource<?, ?> root ){
		String[] split = breakCategory(category);
		if( split == null )
			return null;
		CategoryPropertySource child = new CategoryPropertySource( split[0], root );
		root.addChild(child);
		createCategoryPropertySource(split[1], child);
		return child;
	}
	
	public static CategoryPropertySource<?> findCategoryPropertySource( String category, IJxsePropertySource<?,?> source ){
		String[] split = breakCategory( category);
		if( split == null )
			return null;
		for( IJxsePropertySource<?, ?> ps: source.getChildren() ){
			String name = ps.getComponentName();
			if( name.equals(split[0])){
				CategoryPropertySource<?> child = findCategoryPropertySource( split[2],(IJxsePropertySource<?, ?>)ps);
				if( child == null )
					return (CategoryPropertySource<?>) ps;
			}
		}
		return null;
	}

	/**
	 * Returns the category, by looking for '_8' in the id. Returns null if
	 * no category was found 
	 * @param id
	 * @return
	 */
	public static String[] breakCategory( String category ){
		if( Utils.isNull( category ))
			return null;
		String[] split = category.split("[.]");
		if( split.length <= 1 )
			return null;
		String[] retval = new String[4];
		retval[0] = split[0];
		String rest = category.replace( split[0] + ".", "");
		retval[1] = rest;
		rest = rest.replace( "." + split[split.length - 1], "").trim();
		retval[2] = rest;
		retval[3] = split[ split.length - 1];
		return retval;
	}

	@Override
	public boolean validate(String id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}