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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.jxta.document.Advertisement;
import net.osgi.jxse.properties.IJxseDirectives.Directives;

public class JxseComponent<T extends Object, U extends Object> implements IJxseComponent<T,U>, Comparable< IJxseComponent<?,?>>{

	private T module;
	private Map<U,Object> properties;
	private IJxseComponent<?,?> parent;
	private Collection<Advertisement> advertisements;

	public JxseComponent( T component ) {
		this( null, component, new HashMap<U,Object>() );
	}

	public JxseComponent( T component, Map<U,Object> properties ) {
		this( null, component, properties );
	}

	@SuppressWarnings("unchecked")
	protected JxseComponent( IJxseComponent<?,?> parent, Map<U,Object> properties ) {
		this.module = null;
		this.parent = parent;
		this.properties = (Map<U, Object>) new Properties();
		advertisements = new ArrayList<Advertisement>();
		this.fillProperties(properties);
	}

	@SuppressWarnings("unchecked")
	public JxseComponent( IJxseComponent<?,?> parent, T component, Map<U,Object> properties ) {
		this.module = component;
		this.properties = properties;
		this.parent = parent;
		this.properties = (Map<U, Object>) new Properties();
		advertisements = new ArrayList<Advertisement>();
		this.fillProperties(properties);
	}

	@SuppressWarnings("unchecked")
	public JxseComponent( IJxseComponent<?,?> parent, T component ) {
		this( component, (Map<U, Object>) new Properties() );
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

	public void addAdvertisement( Advertisement advertisement ){
		this.advertisements.add( advertisement );
	}

	public void removedAdvertisement( Advertisement advertisement ){
		this.advertisements.add( advertisement );
	}

	/**
	 * A JXTA service component can use, find or create a number of advertisements. This have to be listed
	 * @return
	 */
	@Override
	public Advertisement[] getAdvertisements(){
		return advertisements.toArray( new Advertisement[ this.advertisements.size() ]);
	}

	/**
	 * Returns true if the component has advertisements
	 * @return
	 */
	@Override
	public boolean hasAdvertisements(){
		return !this.advertisements.isEmpty();
	}

	@Override
	public Object getProperty(Object key) {
		return properties.get(key);
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
