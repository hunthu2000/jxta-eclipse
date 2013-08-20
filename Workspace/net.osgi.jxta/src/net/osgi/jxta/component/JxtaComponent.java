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
package net.osgi.jxta.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.jxta.document.Advertisement;

public class JxtaComponent<T extends Object> implements IJxtaComponent<T>, Comparable< IJxtaComponent<?>>{

	private T module;
	private Map<Object,Object> properties;
	private IJxtaComponent<?> parent;
	private Collection<Advertisement> advertisements;

	public JxtaComponent( T component ) {
		this( null, component, new HashMap<Object,Object>() );
	}

	public JxtaComponent( T component, Map<Object,Object> properties ) {
		this( null, component, properties );
	}

	protected JxtaComponent( IJxtaComponent<?> parent, Map<Object,Object> properties ) {
		this.module = null;
		this.parent = parent;
		this.properties = new Properties();
		advertisements = new ArrayList<Advertisement>();
		this.fillProperties(properties);
	}

	public JxtaComponent( IJxtaComponent<?> parent, T component, Map<Object,Object> properties ) {
		this.module = component;
		this.properties = properties;
		this.parent = parent;
		this.properties = new Properties();
		advertisements = new ArrayList<Advertisement>();
		this.fillProperties(properties);
	}

	public JxtaComponent( IJxtaComponent<?> parent, T component ) {
		this( component, new Properties() );
	}

	/**
	 * Fill the internal properties 
	 * @param properties
	 */
	protected void fillProperties( Map<Object,Object> props){
		Iterator<?> iterator = props.keySet().iterator();	
		Object key, value;
		while( iterator.hasNext()){
			key = iterator.next();
			value = props.get(key);
			if( value != null )
				this.properties.put(key, value);
		}
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
	public IJxtaComponent<?> getParent(){
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

	@Override
	public void putProperty(Object key, Object value ) {
		properties.put( key, value );
	}

	/**
	 * Iterates through all the property keys
	 */
	@Override
	public Iterator<?> iterator() {
		return this.properties.keySet().iterator();
	}

	@Override
	public int compareTo(IJxtaComponent<?> o) {
		return Integer.MAX_VALUE;
	}
}
