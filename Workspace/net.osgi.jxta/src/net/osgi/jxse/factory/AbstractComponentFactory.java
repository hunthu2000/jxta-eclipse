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
package net.osgi.jxse.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractComponentFactory<T extends Object> implements IComponentFactory<T>{

	private Collection<IComponentFactoryListener<T>> listeners;
	private T module;
	private Map<Object,Object> properties;
	private Map<Directives,String> directives;
	
	private Components componentName;
	
	private boolean completed;
	private boolean failed;
	
	protected AbstractComponentFactory( Components componentName) {
		this( componentName, true );
	}

	protected AbstractComponentFactory( Components componentName, boolean fillValues ) {
		listeners = new ArrayList<IComponentFactoryListener<T>>();
		properties = new HashMap<Object, Object>();
		directives = new HashMap<Directives, String>();
		this.completed = false;
		this.failed = false;
		this.componentName = componentName;
		if( fillValues )
			this.fillDefaultValues();
	}

	@Override
	public Components getComponentName() {
		return componentName;
	}
	
	public void addProperty( Object key, Object value ){
		if( value != null )
		  this.properties.put(key, value);
	}
	
	protected Object getProperty( Object key ){
		return this.properties.get(key);
	}

	protected Map<Object,Object> getProperties() {
		return properties;
	}

	protected void addDirective( Directives directive, String value ){
		if( value != null )
		  this.directives.put(directive, value);
	}
	
	protected Object getDirective( Directives directive ){
		return this.directives.get( directive);
	}

	@Override
	public Map<Directives,String> getDirectives() {
		return directives;
	}

	/**
	 * Fill the properties with default values
	 */
	protected abstract void fillDefaultValues();
	
	@Override
	public boolean isCompleted(){
		return this.completed;
	}
	
	
	@Override
	public boolean canCreate() {
		return true;
	}

	protected abstract void onParseDirectivePriorToCreation( Directives directive, String value );
	
	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectives( ){
		if(( directives == null ) || ( directives.isEmpty()))
			return;
		for( Directives directive: directives.keySet())
			this.onParseDirectivePriorToCreation( directive, directives.get( directive ));
	}

	protected abstract void onParseDirectiveAfterCreation( T module, Directives directive, String value );
	
	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectives( T module ){
		if(( directives == null ) || ( directives.isEmpty()))
			return;
		for( Directives directive: directives.keySet()){
			this.onParseDirectiveAfterCreation( module, directive, directives.get( directive ));
		}
	}

	protected abstract T onCreateModule();
	
	@Override
	public T createModule() {
		if( this.completed )
			return module;
		this.parseDirectives();
		this.module = this.onCreateModule();
		this.parseDirectives(module);
		this.setCompleted( true );
		return module;
	}

	protected boolean setCompleted(boolean completed) {
		if( this.failed )
			return false;
		this.completed = completed;
		return completed;
	}

	@Override
	public boolean hasFailed(){
		return this.failed;
	}

	protected void setFailed(boolean failed) {
		this.failed = failed;
		if( failed )
			this.completed = false;
	}

	/**
	 * Set a component if it is valid
	 * @param module
	 * @return
	 */
	protected boolean setJxtaServiceComponent( T module ){
		Logger logger = Logger.getLogger( this.getClass().getName() );
		if( module == null ){
			setFailed(true);
			logger.log( Level.SEVERE, "FAILED to create a component!!!" );
			return false;
		}
		this.module = module;
		this.notifyServiceComponentCompleted(module);
		logger.info( module.getClass().getSimpleName() + " created succesfully" );
		return true;
	}

	@Override
	public T getModule(){
		return module;
	}

	public void addComponentListener( IComponentFactoryListener<T> listener ){
		this.listeners.add(listener);
	}

	public void removeComponentListener( IComponentFactoryListener<T> listener ){
		this.listeners.remove(listener);
	}

	/**
	 * Notify completed service components
	 * @param component
	 */
	protected void notifyServiceComponentCompleted( T component ) {
		JxseComponentEvent<T> event = new JxseComponentEvent<T>( this, component );
		for( IComponentFactoryListener<T> listener: listeners)
			listener.notifyComponentCompleted(event);
	}
}
