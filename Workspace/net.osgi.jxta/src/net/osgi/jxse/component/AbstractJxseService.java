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

import net.osgi.jxse.activator.AbstractActivator;
import net.osgi.jxse.activator.IActivator;
import net.osgi.jxse.activator.IJxseService;
import net.osgi.jxse.context.AbstractServiceContext;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.DefaultPropertySource;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public abstract class AbstractJxseService<T extends Object> extends AbstractActivator
implements IJxseService<T>{

	public static final String S_SERVICE = "Service";
	
	public static final String S_ERR_ILLEGAL_INIT = 
			"The component is initialised but already exists. Please finalise first.";
	public static final String S_ERR_NOT_COMPLETED = 
			"The factory did not create the component. The flag setCompleted must be true, which is usually checked  with setAvailable.";
	
	private T module;
	private IJxseWritePropertySource<IJxseProperties> properties;
	
	private ComponentEventDispatcher dispatcher;

	protected AbstractJxseService( String bundleId, String identifier, String componentName) {
		this( new DefaultPropertySource( bundleId, identifier, componentName),null);
	}

	protected AbstractJxseService( IJxseWritePropertySource<IJxseProperties> properties, T module ) {
		dispatcher = ComponentEventDispatcher.getInstance();
		this.properties = properties;
		this.module = module;
		super.setStatus( Status.AVAILABLE );
		super.initialise();
	}

	protected AbstractJxseService( IComponentFactory<T> factory ) {
		this( (IJxseWritePropertySource<IJxseProperties>) factory.getPropertySource(), factory.getComponent() );
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
	public Date getCreateDate(){
		Object value = this.properties.getProperty( ModuleProperties.CREATE_DATE);
		if( value == null )
			return Calendar.getInstance().getTime();
		return ( Date )value;
	}

	/**
	 * Make public
	 */
	@Override
	public void initialise() {
		super.initialise();
	}

	
	@Override
	protected boolean onInitialising() {
		return true;	
	}

	/**
	 * Make public
	 */
	@Override
	public void finalise() {
		super.finalise();
	}

	@Override
	protected void onFinalising(){
		this.module = null;
	}

	@Override
	public T getModule(){
		return module;
	}
	
	protected void setModule( T module ){
		this.module = module;
	}
	
	
	protected IJxseWritePropertySource<IJxseProperties> getProperties() {
		return properties;
	}

	@Override
	public Object getProperty(Object key) {
		if( key.toString().equals( IActivator.S_STATUS ))
			return super.getStatus();
		return properties.getProperty( (IJxseProperties) key);
	}

	protected void putProperty( IJxseProperties key, Object value ){
		properties.getOrCreateManagedProperty( key, value, false);
	}

	/**
	 * Get the category for the given key
	 * @param key
	 * @return
	 */
	public String getCategory( Object key ){
		return this.properties.getCategory(( IJxseProperties )key );
	}
	
	@Override
	public Iterator<IJxseProperties> iterator() {
		return this.properties.propertyIterator();
	}

	@Override
	protected void notifyListeners(Status previous, Status status) {
		super.notifyListeners(previous, status);
		ComponentChangedEvent event = new ComponentChangedEvent( this, AbstractServiceContext.ServiceChange.STATUS_CHANGE );
		dispatcher.serviceChanged(event);
	}
}