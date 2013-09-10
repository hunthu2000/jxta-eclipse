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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.osgi.jxse.preferences.properties.IJxseDirectives;
import net.osgi.jxse.preferences.properties.IJxsePropertySource;

public abstract class AbstractComponentFactory<T extends Object, U extends Enum<U>, V extends IJxseDirectives> implements IComponentFactory<T,U,V>{

	private Collection<IComponentFactoryListener<T,U,V>> listeners;
	private T module;
	private IJxsePropertySource<U,V> properties;
	
	private boolean completed;
	private boolean failed;
	
	protected AbstractComponentFactory( IJxsePropertySource<U,V> properties ) {
		listeners = new ArrayList<IComponentFactoryListener<T,U,V>>();
		this.properties = properties;
		this.completed = false;
		this.failed = false;
	}

	@Override
	public Components getComponentName() {
		return Components.valueOf( this.properties.getComponentName());
	}
	
	public void addProperty( U id, Object value ){
		this.properties.setProperty( id, value);
	}
	
	@Override
	public IJxsePropertySource<U,V> getPropertySource(){
		return this.properties;
	}

	protected void addDirective( V directive, String value ){
		if( value != null )
		  this.properties.setDirective(directive, value);
	}
	
	protected Object getDirective( V directive ){
		return this.properties.getDirective( directive);
	}

	@Override
	public boolean isCompleted(){
		return this.completed;
	}
	
	
	@Override
	public boolean canCreate() {
		return true;
	}

	protected abstract void onParseDirectivePriorToCreation( V directive, Object value );
	
	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectives( ){
		Iterator<V> iterator = this.properties.directiveIterator();
		V directive;
		while( iterator.hasNext()){
			directive = iterator.next();
			this.onParseDirectivePriorToCreation( directive, properties.getDirective( directive ));
		}
	}

	protected abstract void onParseDirectiveAfterCreation( T module, V directive, Object value );
	
	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectives( T module ){
		Iterator<V> iterator = this.properties.directiveIterator();
		V directive;
		while( iterator.hasNext()){
			directive = iterator.next();
			this.onParseDirectiveAfterCreation( module, directive, properties.getDirective( directive ));
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

	public void addComponentListener( IComponentFactoryListener<T,U,V> listener ){
		this.listeners.add(listener);
	}

	public void removeComponentListener( IComponentFactoryListener<T,U,V> listener ){
		this.listeners.remove(listener);
	}

	/**
	 * Notify completed service components
	 * @param component
	 */
	protected void notifyServiceComponentCompleted( T component ) {
		JxseComponentEvent<T,U,V> event = new JxseComponentEvent<T,U,V>( this, component );
		for( IComponentFactoryListener<T,U,V> listener: listeners)
			listener.notifyComponentCompleted(event);
	}
}
