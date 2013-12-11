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
package net.osgi.jxse.component;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.DefaultPropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class DefaultJxseComponent<T extends Object, U extends Object, V extends IJxseDirectives> implements IJxseComponent<T,U>{

	public static final String S_SERVICE = "Service";
	
	public static final String S_ERR_ILLEGAL_INIT = 
			"The component is initialised but already exists. Please finalise first.";
	public static final String S_ERR_NOT_COMPLETED = 
			"The factory did not create the component. The flag setCompleted must be true, which is usually checked  with setAvailable.";
	
	private T module;
	private IJxseWritePropertySource<U, V> properties;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected DefaultJxseComponent( String bundleId, String identifier, String componentName) {
		this( new DefaultPropertySource( bundleId, identifier, componentName),null);
	}

	protected DefaultJxseComponent( IJxseWritePropertySource<U, V> properties, T module ) {
		this.properties = properties;
		this.module = module;
	}

	protected DefaultJxseComponent( IComponentFactory<T,U,V> factory ) {
		this( (IJxseWritePropertySource<U, V>) factory.getPropertySource(), factory.getModule() );
	}

	/**
	 * Get the id
	 */
	public String getId(){
		return (String) this.properties.getId();
	}

	/**
	 * Get the create date
	 */
	@SuppressWarnings("unchecked")
	public Date getCreateDate(){
		Object value = this.properties.getProperty( (U) ModuleProperties.CREATE_DATE);
		if( value == null )
			return Calendar.getInstance().getTime();
		return ( Date )value;
	}


	@Override
	public T getModule(){
		return module;
	}
	
	protected void setModule( T module ){
		this.module = module;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getProperty(Object key) {
		return properties.getProperty((U) key);
	}

	/**
	 * Get the category for the given key
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getCategory( Object key ){
		return this.properties.getCategory( (U) key );
	}

	protected void putProperty( U key, Object value ){
		properties.getOrCreateManagedProperty( key, value, false);
	}

	
	@Override
	public Iterator<U> iterator() {
		return this.properties.propertyIterator();
	}
}