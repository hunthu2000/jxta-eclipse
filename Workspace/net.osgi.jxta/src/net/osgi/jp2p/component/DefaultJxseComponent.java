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
package net.osgi.jp2p.component;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.properties.DefaultPropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class DefaultJxseComponent<T extends Object> implements IJp2pComponent<T>{

	public static final String S_SERVICE = "Service";
	
	public static final String S_ERR_ILLEGAL_INIT = 
			"The component is initialised but already exists. Please finalise first.";
	public static final String S_ERR_NOT_COMPLETED = 
			"The factory did not create the component. The flag setCompleted must be true, which is usually checked  with setAvailable.";
	
	private T module;
	private IJp2pWritePropertySource<IJp2pProperties> source;
	public IJp2pComponent<?> parent;

	
	protected DefaultJxseComponent( String bundleId, String componentName) {
		this( new DefaultPropertySource( bundleId, componentName), null);
	}

	protected DefaultJxseComponent( IJp2pWritePropertySource<IJp2pProperties> source, T module ) {
		this.source = source;
		this.module = module;
	}

	protected DefaultJxseComponent( IComponentFactory<T> factory ) {
		this( (IJp2pWritePropertySource<IJp2pProperties>) factory.getPropertySource(), factory.getComponent() );
	}

	/**
	 * Get the id
	 */
	public String getId(){
		return (String) this.source.getId();
	}

	/**
	 * Get the create date
	 */
	public Date getCreateDate(){
		Object value = this.source.getProperty( ModuleProperties.CREATE_DATE);
		if( value == null )
			return Calendar.getInstance().getTime();
		return ( Date )value;
	}


	@Override
	public IJp2pPropertySource<IJp2pProperties> getPropertySource() {
		return source;
	}

	@Override
	public T getModule(){
		return module;
	}
	
	protected void setModule( T module ){
		this.module = module;
	}
	
	@Override
	public Object getProperty(Object key) {
		return source.getProperty( (IJp2pProperties) key);
	}

	/**
	 * Get the category for the given key
	 * @param key
	 * @return
	 */
	public String getCategory( Object key ){
		return this.source.getCategory( (IJp2pProperties) key );
	}

	protected void putProperty( IJp2pProperties key, Object value ){
		source.getOrCreateManagedProperty( key, value, false);
	}

	
	@Override
	public Iterator<IJp2pProperties> iterator() {
		return this.source.propertyIterator();
	}

	@Override
	public IJp2pComponent<?> getParent() {
		return parent;
	}

	@Override
	public void setParent(IJp2pComponent<?> parent) {
		this.parent=  parent;
	}
}