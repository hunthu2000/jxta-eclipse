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
package net.osgi.jxse.service.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.jxta.document.Advertisement;
import net.osgi.jxse.activator.AbstractActivator;
import net.osgi.jxse.activator.IActivator;
import net.osgi.jxse.activator.IJxseService;
import net.osgi.jxse.component.ComponentChangedEvent;
import net.osgi.jxse.component.ComponentEventDispatcher;
import net.osgi.jxse.component.IComponentChangedListener.ServiceChange;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;

public abstract class AbstractJxseService<T extends Object, U extends Enum<U>, V extends IJxseDirectives> extends AbstractActivator<IComponentFactory<T,U,V>> 
implements IJxseService<T>{

	public static final String S_SERVICE = "Service";
	
	public static final String S_ERR_ILLEGAL_INIT = 
			"The component is initialised but already exists. Please finalise first.";
	public static final String S_ERR_NOT_COMPLETED = 
			"The factory did not create the component. The flag setCompleted must be true, which is usually checked  with setAvailable.";
	
	private T module;
	private Collection<Advertisement> advertisements;
	
	private Map<Object, Object> properties;
	
	private ComponentEventDispatcher dispatcher;

	protected AbstractJxseService() {
		properties = new HashMap<Object, Object>();
		dispatcher = ComponentEventDispatcher.getInstance();
		advertisements = new ArrayList<Advertisement>();
		super.setStatus( Status.AVAILABLE );
		super.initialise();
	}

	protected AbstractJxseService( T module ) {
		this();
		this.module = module;
	}

	/**
	 * Get the id
	 */
	public String getId(){
		return (String) this.properties.get(ModuleProperties.ID );
	}

	/**
	 * Get the create date
	 */
	public Date getCreateDate(){
		return (Date) this.properties.get(ModuleProperties.CREATE_DATE);
	}

	@Override
	protected boolean onSetAvailable( IComponentFactory<T,U,V> factory ) 
	{
		if( !factory.canCreate() )
			return false;
		if( this.module != null )
			throw new IllegalStateException( S_ERR_ILLEGAL_INIT );
		this.module = factory.createModule();		
		if( !factory.isCompleted() )
			throw new IllegalStateException( S_ERR_NOT_COMPLETED );
		this.fillDefaultValues();
		return false;
	}

	/**
	 * Fill the properties with default values. Note that this is called BEFORE the
	 * service component is loaded, so they are externally overridden 
	 */
	protected abstract void fillDefaultValues( );

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
	public T getModule(){
		return module;
	}
	
	protected void setModule( T module ){
		this.module = module;
	}
	
	@Override
	public Object getProperty(Object key) {
		if( key.toString().equals( IActivator.S_STATUS ))
			return super.getStatus();
		return properties.get(key);
	}

	/**
	 * Put a property only f it is still empty
	 * @param key
	 * @param value
	 * @param skipFilled
	 */
	protected void putProperty( Object key, Object value, boolean skipFilled ){
		if( skipFilled && ( properties.get( key ) != null ))
			return;
		properties.put(key, value);
	}

	@Override
	public void putProperty( Object key, Object value ){
		properties.put(key, value);
	}
	
	/**
	 * Iterates through all the property keys
	 */
	@Override
	public Iterator<?> iterator() {	
		Collection<Object> keys = new ArrayList<Object>( properties.keySet());
		keys.add( IActivator.S_STATUS );
		return keys.iterator();
	}

	@Override
	protected void notifyListeners(Status previous, Status status) {
		super.notifyListeners(previous, status);
		ComponentChangedEvent event = new ComponentChangedEvent( this, ServiceChange.STATUS_CHANGE );
		dispatcher.serviceChanged(event);
	}
}