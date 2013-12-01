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
package net.osgi.jxse.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import net.jxta.document.Advertisement;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.activator.AbstractActivator;
import net.osgi.jxse.activator.IActivator;
import net.osgi.jxse.component.ComponentChangedEvent;
import net.osgi.jxse.component.ComponentEventDispatcher;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.IJxseComponentNode;
import net.osgi.jxse.component.JxseComponent;
import net.osgi.jxse.component.JxseComponentNode;
import net.osgi.jxse.component.IComponentChangedListener.ServiceChange;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public abstract class AbstractServiceContext<T extends Object, U extends Enum<U>, V extends IJxseDirectives> extends AbstractActivator<IComponentFactory<T,U,V>> implements
		IJxseServiceContext<T,U>{

	public static final String S_SERVICE_CONTAINER = "JXSE Container";
	
	private Collection<IJxseComponent<?,?>> children;
	private String identifier;
	private Collection<Advertisement> advertisements;
	private IJxseWritePropertySource<U, V> properties;
	
	private NetworkManager networkManager; 
	
	private ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();

	protected AbstractServiceContext() {
		this.children = new ArrayList<IJxseComponent<?,?>>();
		this.advertisements = new ArrayList<Advertisement>();
	}

	protected AbstractServiceContext( IComponentFactory<T,U,V> factory ) {
		this( factory, false );
	}

	protected AbstractServiceContext( IComponentFactory<T,U,V> factory, boolean skipAvailable ) {
		super( factory, skipAvailable );
		this.children = new ArrayList<IJxseComponent<?,?>>();
		this.advertisements = new ArrayList<Advertisement>();
	}


	protected AbstractServiceContext( IJxseComponent<?,?> parent ) {
		this();
	}

	protected AbstractServiceContext( String identifier ) {
		this();
		this.identifier = identifier;
	}

	
	protected IJxsePropertySource<U, V> getProperties() {
		return properties;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getId() {
		return (String) this.properties.getProperty( (U) ModuleProperties.ID );
	}

	@Override
	protected boolean onInitialising() {
		return false;
	}

	@Override
	protected void onFinalising() {
	}

	protected NetworkManager getNetworkManager() {
		return networkManager;
	}

	/**
	 * Get the create date
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Date getCreateDate(){
		return (Date) this.properties.getProperty( (U) ModuleProperties.CREATE_DATE);
	}

	public void clearModules(){
		children.clear();
	}

	@Override
	protected boolean onSetAvailable( IComponentFactory<T,U,V> obj ) {
		this.properties = (IJxseWritePropertySource<U, V>) obj.getPropertySource();
		return true;
	}

	
	protected void setProperties(IJxseWritePropertySource<U, V> properties) {
		this.properties = properties;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getProperty(Object key) {
		return this.properties.getProperty( (U) key );
	}

	@SuppressWarnings("unchecked")
	@Override
	public void putProperty(Object key, Object value) {
		if( value == null )
			return;
		this.properties.setProperty(( U) key, value);
	}

	public void addAdvertisement( Advertisement advertisement ){
		this.advertisements.add( advertisement );
	}

	public void removeAdvertisement( Advertisement advertisement ){
		this.advertisements.add( advertisement );
	}

	@Override
	public Advertisement[] getAdvertisements() {
		return this.advertisements.toArray(new Advertisement[ this.advertisements.size() ]);
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
	public String getIdentifier() {
		return identifier;
	}

	
	@Override
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public boolean isRoot() {
		return true;
	}
	
	@Override
	public IJxseComponent<?,?> getParent() {
		return null;
	}
	
	@Override
	protected void deactivate() {
		NetworkManager manager = null;
		for( IJxseComponent<?,?> component: this.children ){
			if( component instanceof IActivator ){
				IActivator service = (IActivator )component;
				service.stop();
				if( component.getModule() instanceof NetworkManager ){
					manager = ( NetworkManager )component.getModule();
					manager.stopNetwork();
					return;
				}
			}
		}
	}	

	@Override
	public Collection<IJxseComponent<?,?>> getChildren(){
		return this.children;
	}

	@Override
	public void addChild( IJxseComponent<?,?> child ){
		this.children.add( child );
		if( child.getModule() instanceof NetworkManager )
			this.networkManager = (NetworkManager) child.getModule();
		dispatcher.serviceChanged( new ComponentChangedEvent( this, ServiceChange.CHILD_ADDED ));
	}

	@Override
	public void removeChild( IJxseComponent<?,?> child ){
		this.children.remove( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, ServiceChange.CHILD_REMOVED ));
	}

	@Override
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	/**
	 * Validate the component, and return false if the container cannot proceed.
	 * @param factory
	 * @param component
	 * @return
	 */
	protected boolean validateComponent( IComponentFactory<?,?,?> factory, IJxseComponent<?,?> component ){
		if( !factory.isCompleted() ){
			super.setStatus( Status.AVAILABLE );
			return false;
		}
		return true;
	}

	/**
	 * add a module to the container. returns the JxseComponent, or null if something went wrong
	 * @param module
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static IJxseComponent<?,?> addModule( AbstractServiceContext<?,?,?> context, Object module ){
		if( module instanceof Advertisement ){
			context.addAdvertisement( (Advertisement) module );
			return new JxseComponent( context, (Advertisement)module );
		}
		IJxseComponent<Object,?> component = null;
		if( module instanceof IJxseComponent )
			component = (IJxseComponent<Object,?>) module;
		else
			component = new JxseComponent( context, module );

		component.putProperty( ModuleProperties.CREATE_DATE, Calendar.getInstance().getTime() );
		if( module instanceof NetworkManager ){
			IJxseComponentNode<Object,?> node = new JxseComponentNode( context, component );
			NetworkManager manager = ( NetworkManager )module;
			try {
				node.addChild( new JxseComponent( context, manager.getConfigurator() ));
			} catch (IOException e) {
				e.printStackTrace();
			}
			context.addChild(node);
			return node;
		}else{
			context.addChild( component );
			return component;
		}
	}

	protected static void removeModule( AbstractServiceContext<?,?,?> context, Object module ){
		if( module instanceof Advertisement ){
			context.removeAdvertisement( (Advertisement) module );
			return;
		}
		Collection<IJxseComponent<?,?>> temp = new ArrayList<IJxseComponent<?,?>>( context.getChildren() );
		for( IJxseComponent<?,?> component: temp ){
			if( component.getModule().equals( module ))
				context.getChildren().remove(component);
		}
	}

	@Override
	public Iterator<U> iterator(){
		return this.properties.propertyIterator();
	}

}