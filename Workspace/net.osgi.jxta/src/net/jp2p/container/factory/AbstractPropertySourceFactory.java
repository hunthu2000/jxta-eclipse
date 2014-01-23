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

import java.util.Iterator;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.properties.IJp2pDirectives;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.ManagedProperty;

public abstract class AbstractPropertySourceFactory<T extends Object> implements IPropertySourceFactory<IJp2pComponent<T>>{

	public static final String S_FACTORY = "Factory:";
	
	private IJp2pPropertySource<IJp2pProperties> parentSource;//Needed for it triggers the child source
	private IJp2pPropertySource<IJp2pProperties> source;
	
	private boolean canCreate;
	private IContainerBuilder container;
	private int weight;
	
	protected AbstractPropertySourceFactory( IContainerBuilder container ) {
		this( container, null );
	}

	protected AbstractPropertySourceFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parentSource ) {
		this.canCreate = false;
		this.parentSource = parentSource;
		this.container = container;
		this.weight = Integer.MAX_VALUE;
	}

	protected IJp2pPropertySource<IJp2pProperties> getParentSource() {
		return parentSource;
	}

	@Override
	public IJp2pPropertySource<IJp2pProperties> getPropertySource(){
		return this.source;
	}

	/**
	 * Set the source manually
	 * @param source
	 */
	protected void setSource(IJp2pPropertySource<IJp2pProperties> source) {
		this.source = source;
	}

	@Override
	public String getComponentName() {
		return source.getComponentName();
	}

	/**
	 * This method is called after the property sources have been created,
	 * to allow other factories to be added as well.
	 */
	@Override
	public void extendContainer(){ /* DO NOTHING */}

	/**
	 * Parse the properties
	 */
	public void parseProperties(){
		Iterator<IJp2pProperties> iterator = source.propertyIterator();
		while( iterator.hasNext()){
			this.onParseProperty( source.getManagedProperty( iterator.next()));
		}
	}
	protected void onParseProperty( ManagedProperty<IJp2pProperties, Object> property ){/* DO NOTHING */}
	
	/**
	 * Get the builder
	 * @return
	 */
	protected IContainerBuilder getBuilder() {
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
	protected abstract IJp2pPropertySource<IJp2pProperties> onCreatePropertySource();
	
	@Override
	public IJp2pPropertySource<IJp2pProperties> createPropertySource() {
		if( this.source == null ){
			this.source = this.onCreatePropertySource();
			this.updateState( BuilderEvents.PROPERTY_SOURCE_PREPARED );
		}
		return source;
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
	protected void onParseDirectivePriorToCreation( IJp2pDirectives directive, Object value ){/* DO NOTHING*/}
	
	/**
	 * All the directives are parsed after the factory is created 
	 * @param directive
	 * @param value
	 */
	protected void onParseDirectiveAfterCreation( IJp2pDirectives directive, Object value ){/* DO NOTHING*/}
	
	/**
	 * Actions needed to create the component
	 * @param properties
	 * @return
	 */
	protected abstract IJp2pComponent<T> onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties);
	
	/**
	 * Allow an update of the 
	 * @param event
	 */
	public synchronized void updateState( BuilderEvents event ){
		try{
			this.container.updateRequest( new ComponentBuilderEvent<IJp2pComponent<T>>( this, event ));
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}
	
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		switch( event.getBuilderEvent()){
		default:
			break;
		}
	}

	/**
	 * Returns true if the given factory is an immediate child of this one
	 * @param factory
	 * @return
	 */
	protected boolean isChildFactory( IPropertySourceFactory<?> factory ){
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
	public static boolean isPropertySourceFactory( IJp2pComponents component, IPropertySourceFactory<?> factory ){
		if( component == null )
			return false;
		return component.toString().equals(factory.getComponentName() );
	}
}