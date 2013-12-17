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

import net.osgi.jxse.activator.IActivator;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;

public abstract class AbstractComponentFactory<T extends Object, U extends IJxseProperties> implements IComponentFactory<T,U>{

	public static final String S_FACTORY = "Factory:";
	
	private Collection<IComponentFactoryListener<T,U>> listeners;
	private T module;
	private IJxsePropertySource<U> properties;
	
	private boolean canCreate;
	private boolean completed;
	private boolean failed;

	protected AbstractComponentFactory( IJxsePropertySource<U> properties ) {
		this( properties, true );
	}

	protected AbstractComponentFactory( IJxsePropertySource<U> properties, boolean canCreate ) {
		listeners = new ArrayList<IComponentFactoryListener<T,U>>();
		this.properties = properties;
		this.canCreate = canCreate;
		this.completed = false;
		this.failed = false;
	}

	@Override
	public Components getComponent() {
		return Components.valueOf( StringStyler.styleToEnum( this.properties.getComponentName()));
	}
	
	protected void setPropertySource( IJxsePropertySource<U> properties ){
		this.properties = properties;
	}
	
	@Override
	public IJxsePropertySource<U> getPropertySource(){
		return this.properties;
	}

	@Override
	public boolean isCompleted(){
		return this.completed;
	}
	
	
	@Override
	public boolean canCreate() {
		return this.canCreate;
	}

	protected void setCanCreate(boolean canCreate) {
		this.canCreate = canCreate;
	}


	/**
	 * All the directives are parsed prior to creating the factory 
	 * @param directive
	 * @param value
	 */
	protected void onParseDirectivePriorToCreation( IJxseDirectives directive, Object value ){/* DO NOTHING*/}
	
	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectivesPrior( ){
		Iterator<IJxseDirectives> iterator = this.properties.directiveIterator();
		IJxseDirectives directive;
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
	protected void onParseDirectiveAfterCreation( IJxseDirectives directive, Object value ){/* DO NOTHING*/}
	
	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectivesAfter(){
		Iterator<IJxseDirectives> iterator = this.properties.directiveIterator();
		IJxseDirectives directive;
		while( iterator.hasNext()){
			directive = iterator.next();
			this.onParseDirectiveAfterCreation( directive, properties.getDirective( directive ));
		}
	}

	protected abstract T onCreateModule( IJxsePropertySource<U> properties);
	
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

	/**
	 * Returns true if the module is activated
	 * @return
	 */
	public boolean moduleActive(){
		if( this.module == null )
			return false;
		if(!( this.module instanceof IActivator ))
			return true;
		IActivator activator = ( IActivator )this.module;
		return activator.isActive();
	}

	public void addComponentListener( IComponentFactoryListener<T,U> listener ){
		this.listeners.add(listener);
	}

	public void removeComponentListener( IComponentFactoryListener<T,U> listener ){
		this.listeners.remove(listener);
	}

	/**
	 * Notify completed service components
	 * @param component
	 */
	protected void notifyServiceComponentCompleted( T component ) {
		JxseComponentEvent<T,U> event = new JxseComponentEvent<T,U>( this, component );
		for( IComponentFactoryListener<T,U> listener: listeners)
			listener.notifyComponentCompleted(event);
	}

	@Override
	public String toString() {
		return S_FACTORY + this.getPropertySource().getComponentName() + super.toString();
	}	
}
