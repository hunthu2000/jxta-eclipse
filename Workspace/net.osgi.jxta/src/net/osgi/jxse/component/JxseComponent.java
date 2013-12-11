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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.ManagedProperty;

public class JxseComponent<T extends Object, U extends Object> implements IJxseComponent<T,U>, Comparable< IJxseComponent<?,?>>{

	private T module;
	private Map<U,Object> properties;
	private IJxseComponent<?,?> parent;

	public JxseComponent( T component ) {
		this( null, component );
	}

	@SuppressWarnings("unchecked")
	public JxseComponent( IJxseComponent<?,?> parent, T component ) {
		this.module = component;
		this.properties = new HashMap<U,Object>();
		this.parent = parent;
		this.properties = (Map<U, Object>) new Properties();
		this.fillProperties(properties);
	}

	/**
	 * Fill the internal properties 
	 * @param properties
	 */
	@SuppressWarnings("unchecked")
	protected void fillProperties( Map<U,Object> props){
		Iterator<?> iterator = props.keySet().iterator();	
		U key;
		Object value;
		while( iterator.hasNext()){
			key = (U) iterator.next();
			value = props.get(key);
			if( value != null )
				this.properties.put(key, value);
		}
	}

	@Override
	public String getId() {
		return ( String )this.properties.get(Directives.ID);
	}

	/**
	 * Get the create date
	 */
	@Override
	public Date getCreateDate(){
		return (Date) this.properties.get(ModuleProperties.CREATE_DATE);
	}

	/**
	 * Return true if the component is a root
	 * @return
	 */
	public boolean isRoot(){
		return ( this.parent == null );
	}
	/**
	 * Get the parent of the component
	 * @return
	 */
	public IJxseComponent<?,?> getParent(){
		return parent;
	}

	@Override
	public T getModule() {
		return this.module;
	}

	@Override
	public Object getProperty(Object key) {
		return properties.get(key);
	}

	/**
	 * Get the category for the given key
	 * @param key
	 * @return
	 */
	public String getCategory( Object key ){
		return ManagedProperty.S_DEFAULT_CATEGORY;
	}

	protected void putProperty(U key, Object value ) {
		properties.put( key, value );
	}

	@Override
	public int compareTo(IJxseComponent<?,?> o) {
		return Integer.MAX_VALUE;
	}

	@Override
	public Iterator<U> iterator() {
		return properties.keySet().iterator();
	}
}
