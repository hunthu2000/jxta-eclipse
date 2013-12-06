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

import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;

public abstract class AbstractComponentFactory<T extends Object, U extends IJxseProperties, V extends IJxseDirectives> implements IComponentFactory<T,U,V>{

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
		return Components.valueOf( StringStyler.styleToEnum( this.properties.getComponentName()));
	}
	
	protected void setPropertySource( IJxsePropertySource<U,V> properties ){
		this.properties = properties;
	}
	
	@Override
	public IJxsePropertySource<U,V> getPropertySource(){
		return this.properties;
	}

	@Override
	public boolean isCompleted(){
		return this.completed;
	}
	
	
	@Override
	public boolean canCreate() {
		return true;
	}

	/**
	 * All the directives are parsed prior to creating the factory 
	 * @param directive
	 * @param value
	 */
	protected void onParseDirectivePriorToCreation( V directive, Object value ){/* DO NOTHING*/}
	
	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectivesPrior( ){
		Iterator<V> iterator = this.properties.directiveIterator();
		V directive;
		while( iterator.hasNext()){
			directive = iterator.next();
			this.onParseDirectivePriorToCreation( directive, properties.getDirective( directive ));
		}
	}

	/**
	 * All the directives are parsed after the factory is created 
	 * @param directive
	 * @param value
	 */
	protected void onParseDirectiveAfterCreation( V directive, Object value ){/* DO NOTHING*/}
	
	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectivesAfter(){
		Iterator<V> iterator = this.properties.directiveIterator();
		V directive;
		while( iterator.hasNext()){
			directive = iterator.next();
			this.onParseDirectiveAfterCreation( directive, properties.getDirective( directive ));
		}
	}

	protected abstract T onCreateModule( IJxsePropertySource<U,V> properties);
	
	@Override
	public T createModule() {
		if( this.completed )
			return module;
		this.parseDirectivesPrior();
		this.module = this.onCreateModule( this.properties);
		if( this.module == null )
			return null;
		this.parseDirectivesAfter();
		return module;
	}

	/**
	 * The completion is not necessarily the same as creating the module. This method has to 
	 * be called separately;
	 * @return
	 */
	public boolean complete(){
		this.setCompleted( true );
		return this.completed;
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
