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

import net.osgi.jp2p.activator.AbstractActivator;
import net.osgi.jp2p.activator.IActivator;
import net.osgi.jp2p.activator.IJp2pService;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.container.AbstractServiceContainer;
import net.osgi.jp2p.properties.AbstractJp2pPropertySource;
import net.osgi.jp2p.properties.DefaultPropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.utils.StringProperty;

public abstract class AbstractJp2pService<T extends Object> extends AbstractActivator
implements IJp2pService<T>{

	public static final String S_SERVICE = "Service";
	
	public static final String S_ERR_ILLEGAL_INIT = 
			"The component is initialised but already exists. Please finalise first.";
	public static final String S_ERR_NOT_COMPLETED = 
			"The factory did not create the component. The flag setCompleted must be true, which is usually checked  with setAvailable.";
	
	private T component;
	private IJp2pWritePropertySource<IJp2pProperties> source;
	
	private ComponentEventDispatcher dispatcher;

	protected AbstractJp2pService( String bundleId, String componentName) {
		this( new DefaultPropertySource( bundleId, componentName),null);
	}

	protected AbstractJp2pService( IJp2pWritePropertySource<IJp2pProperties> source, T module ) {
		dispatcher = ComponentEventDispatcher.getInstance();
		this.source = source;
		this.component = module;
		super.setStatus( Status.AVAILABLE );
		super.initialise();
	}

	protected AbstractJp2pService( IComponentFactory<T> factory ) {
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
		this.component = null;
	}

	@Override
	public T getModule(){
		return component;
	}
	
	protected void setComponent( T module ){
		this.component = module;
	}
	
	@Override
	public Object getProperty(Object key) {
		if( key.toString().equals( IActivator.S_STATUS ))
			return super.getStatus();
		return AbstractJp2pPropertySource.getExtendedProperty(source, (IJp2pProperties) key);
	}

	protected void putProperty( IJp2pProperties key, Object value ){
		String[] split = key.toString().split("[.]");
		StringProperty id = new StringProperty( split[ split.length - 1]);
		ManagedProperty<IJp2pProperties, Object> mp = source.getOrCreateManagedProperty(id, value, false);
		mp.setValue(value);
	}

	/**
	 * Get the category for the given key
	 * @param key
	 * @return
	 */
	public String getCategory( Object key ){
		return this.source.getCategory(( IJp2pProperties )key );
	}
	
	@Override
	public Iterator<IJp2pProperties> iterator() {
		return AbstractJp2pPropertySource.getExtendedIterator(source);
	}

	@Override
	protected void notifyListeners(Status previous, Status status) {
		super.notifyListeners(previous, status);
		ComponentChangedEvent event = new ComponentChangedEvent( this, AbstractServiceContainer.ServiceChange.STATUS_CHANGE );
		dispatcher.serviceChanged(event);
	}
}