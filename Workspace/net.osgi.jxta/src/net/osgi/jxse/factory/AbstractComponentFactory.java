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

import java.util.Iterator;

import net.osgi.jxse.activator.IActivator;
import net.osgi.jxse.activator.IActivator.Status;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.IJxseComponentNode;
import net.osgi.jxse.component.JxseComponentNode;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public abstract class AbstractComponentFactory<T extends Object> implements IComponentFactory<IJxseComponent<T>>{

	public static final String S_FACTORY = "Factory:";
	public static final String S_ERR_CREATION_EXCEPTION = "The factory cannot be created, because it is not ready yet";
	
	private IJxseComponent<T> component;
	private IJxsePropertySource<IJxseProperties> parentSource;
	private IJxsePropertySource<IJxseProperties> source;
	
	private boolean canCreate;
	private boolean completed;
	private boolean failed;
	private BuilderContainer container;
	private int weight;

	protected AbstractComponentFactory( BuilderContainer container ) {
		this( container, true );
	}

	protected AbstractComponentFactory( BuilderContainer container, boolean canCreate) {
		this( container, null, canCreate );
	}

	protected AbstractComponentFactory( BuilderContainer container, IJxsePropertySource<IJxseProperties> parentSource ) {
		this( container, parentSource, true );
	}

	protected AbstractComponentFactory( BuilderContainer container, IJxsePropertySource<IJxseProperties> parentSource, boolean canCreate ) {
		this.canCreate = canCreate;
		this.completed = false;
		this.failed = false;
		this.parentSource = parentSource;
		this.container = container;
		this.weight = Integer.MAX_VALUE;
	}

	protected IJxsePropertySource<IJxseProperties> getParentSource() {
		return parentSource;
	}

	@Override
	public IJxsePropertySource<IJxseProperties> getPropertySource(){
		return this.source;
	}

	/**
	 * Set the source manually
	 * @param source
	 */
	protected void setSource(IJxsePropertySource<IJxseProperties> source) {
		this.source = source;
	}

	/**
	 * This method is called after the property sources have been created,
	 * to allow other factories to be added as well.
	 */
	public void extendContainer(){ /* DO NOTHING */}

	protected BuilderContainer getContainer() {
		return container;
	}

	/**
	 * Get the weight of the factory. By default, the context factory is zero, startup service is one
	 * @return
	 */
	public int getWeight(){
		return weight;
	}

	protected void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	 * Is called upon creating the property source.
	 * @return
	 */
	protected abstract IJxsePropertySource<IJxseProperties> onCreatePropertySource();
	
	@Override
	public IJxsePropertySource<IJxseProperties> createPropertySource() {
		if( this.source == null )
			this.source = this.onCreatePropertySource();
		return source;
	}

	@Override
	public boolean isCompleted(){
		return this.completed;
	}
	
	@Override
	public final boolean canCreate() {
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
		Iterator<IJxseDirectives> iterator = this.source.directiveIterator();
		IJxseDirectives directive;
		while( iterator.hasNext()){
			directive = iterator.next();
			this.onParseDirectivePriorToCreation( directive, source.getDirective( directive ));
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
		Iterator<IJxseDirectives> iterator = this.source.directiveIterator();
		IJxseDirectives directive;
		while( iterator.hasNext()){
			directive = iterator.next();
			this.onParseDirectiveAfterCreation( directive, source.getDirective( directive ));
		}
	}

	/**
	 * Actions needed to create the component
	 * @param properties
	 * @return
	 */
	protected abstract IJxseComponent<T> onCreateComponent( IJxsePropertySource<IJxseProperties> properties);
	
	/**
	 * Create the component. By default, the factory can do this internally.
	 * If this has to be done externally, it has to be specifically implemented 
	 * @return
	 */
	protected IJxseComponent<T> createComponent() {
		if( this.completed )
			return component;
		if(!this.canCreate() )
			throw new FactoryException( S_ERR_CREATION_EXCEPTION );
		this.parseDirectivesPrior();
		this.component = this.onCreateComponent( this.source);
		if( this.component == null )
			return null;
		this.parseDirectivesAfter();
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
	public IJxseComponent<T> getComponent(){
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

	/**
	 * Allow an update of the 
	 * @param event
	 */
	public void updateState( BuilderEvents event ){
		try{
			this.container.updateRequest( new ComponentBuilderEvent<IJxseComponent<T>>( this, event ));
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		switch( event.getBuilderEvent()){
		case COMPONENT_CREATED:
			if(( this.component == null) || (!( this.component instanceof IJxseComponentNode )))
				return;
			IJxseComponentNode<IJxseProperties> comp = (IJxseComponentNode<IJxseProperties>) component;		
			IComponentFactory<?>factory = event.getFactory();
			if( !isChildFactory( factory ))
				return;
			JxseComponentNode.addModule( comp, factory.getComponent());
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
		IJxsePropertySource<IJxseProperties> source = factory.getPropertySource();
		if(( source == null ) || ( source.getParent() == null ))
			return false;
		return ( source.getParent().equals( this.getPropertySource()));
	}

	@Override
	public String toString() {
		return S_FACTORY + this.getPropertySource().getComponentName() + super.toString();
	}	
	
	/**
	 * Returns true if the factory has the same component name as the given one.
	 * @param component
	 * @param factory
	 * @return
	 */
	public static boolean isComponentFactory( Components component, IComponentFactory<?> factory ){
		if( component == null )
			return false;
		return component.toString().equals(factory.getComponentName() );
	}
	
	/**
	 * Helper routine to start a component that is an IActivator instance
	 * @return
	 */
	protected boolean startComponent(){
		Object component = this.createComponent();
		if(!( component instanceof IActivator ))
			return false;
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