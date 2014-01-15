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
package net.osgi.jp2p.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import net.osgi.jp2p.activator.AbstractActivator;
import net.osgi.jp2p.activator.IActivator;
import net.osgi.jp2p.component.ComponentChangedEvent;
import net.osgi.jp2p.component.ComponentEventDispatcher;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.container.Jp2pContainerPropertySource;
import net.osgi.jp2p.container.Swarm;
import net.osgi.jp2p.properties.AbstractJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;
import net.osgi.jp2p.utils.StringStyler;

public class AbstractServiceContainer<T extends Object> extends AbstractActivator 
implements	IJxseServiceContainer<T>{

	public static enum ServiceChange{
		CHILD_ADDED,
		CHILD_REMOVED,
		PEERGROUP_ADDED,
		PEERGROUP_REMOVED,
		STATUS_CHANGE,
		COMPONENT_EVENT;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public static final String S_SERVICE_CONTAINER = "JXSE Container";
	
	private Collection<IJp2pComponent<?>> children;
	private IJp2pPropertySource<IJp2pProperties> source;
	
	//Takes care of all the messaging through the container
	private ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
	
	private Swarm<?> swarm;

	protected AbstractServiceContainer( String bundleId, String identifier) {
		this( (IJp2pPropertySource<IJp2pProperties>) new Jp2pContainerPropertySource( bundleId ));
	}

	protected AbstractServiceContainer( IJp2pPropertySource<IJp2pProperties> source ) {
		super();
		this.children = new ArrayList<IJp2pComponent<?>>();
		this.source = source;
		swarm = new Swarm<Object>();
	}

	@Override
	public IJp2pPropertySource<IJp2pProperties> getPropertySource() {
		return source;
	}

	@Override
	public String getId() {
		return (String) this.source.getDirective( Directives.ID );
	}

	/**
	 * Get the dispatcher for this container
	 * @return
	 */
	public ComponentEventDispatcher getDispatcher(){
		return dispatcher;
	}
	
	@Override
	protected boolean onInitialising() {
		return false;
	}

	@Override
	protected void onFinalising() {
	}

	public Swarm<?> getSwarm() {
		return swarm;
	}

	/**
	 * Get the create date
	 */
	@Override
	public Date getCreateDate(){
		return (Date) this.source.getProperty( ModuleProperties.CREATE_DATE);
	}

	public void clearModules(){
		children.clear();
	}

	protected void setProperties(IJp2pWritePropertySource<IJp2pProperties> properties) {
		this.source = properties;
	}

	@Override
	public Object getProperty(Object key) {
		return this.source.getProperty( (IJp2pProperties) key );
	}

	protected void putProperty(Object key, Object value) {
		if( value == null )
			return;
		((AbstractJp2pWritePropertySource) this.source).setProperty( (IJp2pProperties) key, value);
	}

	/**
	 * Get the category for the given key
	 * @param key
	 * @return
	 */
	public String getCategory( Object key ){
		return this.source.getCategory( (IJp2pProperties) key );
	}

	@Override
	public String getIdentifier() {
		return this.source.getDirective( Directives.NAME );
	}
	
	@Override
	public boolean isRoot() {
		return true;
	}
	
	@Override
	protected void deactivate() {
		for( IJp2pComponent<?> component: this.children ){
			if( component instanceof IActivator ){
				IActivator service = (IActivator )component;
				service.stop();
			}
		}
	}	

	@Override
	public Collection<IJp2pComponent<?>> getChildren(){
		return this.children;
	}

	@Override
	public void addChild( IJp2pComponent<?> child ){
		this.children.add( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, AbstractServiceContainer.ServiceChange.CHILD_ADDED ));
	}

	@Override
	public void removeChild( IJp2pComponent<?> child ){
		this.children.remove( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, AbstractServiceContainer.ServiceChange.CHILD_REMOVED ));
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
	protected boolean validateComponent( IComponentFactory<?> factory, IJp2pComponent<?> component ){
		if( !factory.isCompleted() ){
			super.setStatus( Status.AVAILABLE );
			return false;
		}
		return true;
	}

	protected static void removeModule( AbstractServiceContainer<?> context, Object module ){
		Collection<IJp2pComponent<?>> temp = new ArrayList<IJp2pComponent<?>>( context.getChildren() );
		for( IJp2pComponent<?> component: temp ){
			if( component.getModule().equals( module ))
				context.getChildren().remove(component);
		}
	}

	@Override
	public Iterator<IJp2pProperties> iterator(){
		return this.source.propertyIterator();
	}

	@Override
	public T getModule() {
		// TODO Auto-generated method stub
		return null;
	}
}