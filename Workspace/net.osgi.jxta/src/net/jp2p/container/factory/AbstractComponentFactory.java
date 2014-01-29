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
package net.jp2p.container.factory;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.jp2p.container.activator.IActivator;
import net.jp2p.container.activator.IActivator.Status;
import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.component.IJp2pComponentNode;
import net.jp2p.container.component.Jp2pComponentNode;
import net.jp2p.container.properties.AbstractJp2pPropertySource;
import net.jp2p.container.properties.IJp2pDirectives;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IJp2pDirectives.Directives;
import net.jp2p.container.properties.IJp2pProperties.Jp2pProperties;

public abstract class AbstractComponentFactory<T extends Object> extends AbstractPropertySourceFactory<T> implements IComponentFactory<IJp2pComponent<T>>{

	public static final String S_FACTORY = "Factory:";
	public static final String S_ERR_CREATION_EXCEPTION = "The factory cannot be created, because it is not ready yet";
	
	private IJp2pComponent<T> component;
	
	private boolean completed;
	private boolean failed;
	
	private Stack<Object> stack;

	protected AbstractComponentFactory( IContainerBuilder container ) {
		this( container, null );
	}

	protected AbstractComponentFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parentSource ) {
		super( container, parentSource );
		this.completed = false;
		this.failed = false;
		stack = new Stack<Object>();
	}

	@Override
	public boolean isCompleted(){
		return this.completed;
	}

	/**
	 * Some services need to start prior to the creation of properties. This can be performed here.
	 */
	public void earlyStart(){ /* DO NOTHING */};

	/**
	 * All the directives are parsed prior to creating the factory 
	 * @param directive
	 * @param value
	 */
	protected void onParseDirectivePriorToCreation( IJp2pDirectives directive, Object value ){/* DO NOTHING*/}
	
	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectivesPrior( ){
		Iterator<IJp2pDirectives> iterator = super.getPropertySource().directiveIterator();
		IJp2pDirectives directive;
		while( iterator.hasNext()){
			directive = iterator.next();
			this.onParseDirectivePriorToCreation( directive, super.getPropertySource().getDirective( directive ));
		}
	}

	/**
	 * All the directives are parsed after the factory is created 
	 * @param directive
	 * @param value
	 */
	protected void onParseDirectiveAfterCreation( IJp2pDirectives directive, Object value ){/* DO NOTHING*/}
	
	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectivesAfter(){
		Iterator<IJp2pDirectives> iterator = super.getPropertySource().directiveIterator();
		IJp2pDirectives directive;
		while( iterator.hasNext()){
			directive = iterator.next();
			this.onParseDirectiveAfterCreation( directive, super.getPropertySource().getDirective( directive ));
		}
	}

	/**
	 * Actions needed to create the component
	 * @param properties
	 * @return
	 */
	protected abstract IJp2pComponent<T> onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties);
	
	/**
	 * Create the component. By default, the factory can do this internally.
	 * If this has to be done externally, it has to be specifically implemented.
	 * The Creation follows a number of steps:
	 * 1: if the blockCreation directive is set, the factory will return null
	 * 2: If the factory is completed, the component is not created
	 * 3: if the factory cannot the create the component, an exception is given
	 * 4: the component is created and events are spawned 
	 * @return
	 */
	protected synchronized IJp2pComponent<T> createComponent() {
		boolean blockCreation = AbstractJp2pPropertySource.getBoolean( super.getPropertySource(), Directives.BLOCK_CREATION );
		if( blockCreation )
			return null;
		
		if( this.completed )
			return component;
		if(!this.canCreate() )
			throw new FactoryException( S_ERR_CREATION_EXCEPTION );
		this.parseDirectivesPrior();
		this.component = this.onCreateComponent( super.getPropertySource() );
		if( this.component == null )
			return null;
		this.parseDirectivesAfter();
		if( component instanceof IJp2pComponentNode ){
			while( stack.size() >0 ){
				Jp2pComponentNode.addModule( (IJp2pComponentNode<?>) component, stack.pop() );
			}
		}
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) component.getPropertySource();
		source.setProperty( Jp2pProperties.CREATE_DATE, Calendar.getInstance().getTime() );
		updateState( BuilderEvents.COMPONENT_CREATED );
			
		complete();
		updateState( BuilderEvents.FACTORY_COMPLETED );
		return component;
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
	public IJp2pComponent<T> getComponent(){
		return component;
	}

	/**
	 * Returns true if the module is activated
	 * @return
	 */
	public boolean componentActive(){
		if( this.component == null )
			return false;
		if(!( this.component instanceof IActivator ))
			return true;
		IActivator activator = ( IActivator )this.component;
		return activator.isActive();
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		switch( event.getBuilderEvent()){
		case COMPONENT_CREATED:
			IComponentFactory<?>factory = (IComponentFactory<?>) event.getFactory();
			if( isChildFactory( factory )){
				if( component == null )
				  stack.push( factory.getComponent() );
				else{
					Jp2pComponentNode.addModule( (IJp2pComponentNode<?>) component, factory.getComponent() );
				}
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Returns true if the given factory is an immediate child of this one
	 * @param factory
	 * @return
	 */
	protected boolean isChildFactory( IComponentFactory<?> factory ){
		if( factory == null )
			return false;
		IJp2pPropertySource<IJp2pProperties> source = factory.getPropertySource();
		if(( source == null ) || ( source.getParent() == null ))
			return false;
		return ( source.getParent().equals( this.getPropertySource()));
	}

	@Override
	public String toString() {
		return S_FACTORY + this.getPropertySource().getComponentName() + " {" + super.toString() + "}";
	}	
	
	/**
	 * Returns true if the factory has the same component name as the given one.
	 * @param component
	 * @param factory
	 * @return
	 */
	public static boolean isComponentFactory( IJp2pComponents component, IPropertySourceFactory<?> factory ){
		if( component == null )
			return false;
		return component.toString().equals(factory.getComponentName() );
	}
	
	/**
	 * Helper routine to start a component that is an IActivator instance
	 * @return
	 */
	protected boolean startComponent(){
		this.updateState( BuilderEvents.COMPONENT_PREPARED );
		ExecutorService executor = Executors.newCachedThreadPool();
		StartRunnable runnable = new StartRunnable( this );
		executor.execute( runnable );
		return true;
	}

	/**
	 * Create and start the component
	 * @return
	 */
	protected synchronized boolean createAndStartComponent(){
		Object component = this.createComponent();
		if(!( component instanceof IActivator ))
			return true;
		boolean retval = false;
		try{
			IActivator activator = (IActivator)component;
			if( !activator.getStatus().equals( Status.INITIALISED))
				return false;
			retval = activator.start();
			this.updateState( BuilderEvents.COMPONENT_STARTED);
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return retval;
	}
}

class StartRunnable implements Runnable{

	private AbstractComponentFactory<?> factory;
	
	private boolean result;
	
	StartRunnable( AbstractComponentFactory<?> factory ){
		this.factory = factory;
		this.result = false;
	}
	
	boolean isResult() {
		return result;
	}


	@Override
	public void run(){
		result = this.factory.createAndStartComponent();
	}
	
	
}