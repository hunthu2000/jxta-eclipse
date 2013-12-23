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
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.activator.AbstractActivator;
import net.osgi.jxse.activator.IActivator;
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.component.ComponentChangedEvent;
import net.osgi.jxse.component.ComponentEventDispatcher;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.IJxseComponentNode;
import net.osgi.jxse.component.JxseComponent;
import net.osgi.jxse.component.JxseComponentNode;
import net.osgi.jxse.factory.FactoryNode;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.utils.StringStyler;

public class AbstractServiceContext extends AbstractActivator 
implements	IJxseServiceContext<NetworkManager>{

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
	
	private Collection<IJxseComponent<?,?>> children;
	private IJxsePropertySource<IJxseProperties> properties;
	
	private NetworkManager networkManager; 
	
	private ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
	
	private Swarm swarm;

	protected AbstractServiceContext( String bundleId, String identifier) {
		this( (IJxsePropertySource<IJxseProperties>) new JxseContextPropertySource( bundleId, identifier));
	}

	protected AbstractServiceContext( IJxsePropertySource<IJxseProperties> source ) {
		super();
		this.children = new ArrayList<IJxseComponent<?,?>>();
		this.properties = source;
		swarm = new Swarm();
	}

	public IJxsePropertySource<IJxseProperties> getProperties() {
		return properties;
	}

	@Override
	public String getId() {
		return (String) this.properties.getDirective( Directives.ID );
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

	
	public Swarm getSwarm() {
		return swarm;
	}

	/**
	 * Get the create date
	 */
	@Override
	public Date getCreateDate(){
		return (Date) this.properties.getProperty( ModuleProperties.CREATE_DATE);
	}

	public void clearModules(){
		children.clear();
	}

	protected void setProperties(IJxseWritePropertySource<IJxseProperties> properties) {
		this.properties = properties;
	}

	@Override
	public Object getProperty(Object key) {
		return this.properties.getProperty( (IJxseProperties) key );
	}

	protected void putProperty(Object key, Object value) {
		if( value == null )
			return;
		((AbstractJxseWritePropertySource<IJxseProperties>) this.properties).setProperty( (IJxseProperties) key, value);
	}

	/**
	 * Get the category for the given key
	 * @param key
	 * @return
	 */
	public String getCategory( Object key ){
		return this.properties.getCategory( (IJxseProperties) key );
	}

	@Override
	public String getIdentifier() {
		return this.properties.getIdentifier();
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
		if( child.getModule() instanceof PeerGroup ){
			this.swarm.addPeerGroup(( PeerGroup )child.getModule());
		}
		dispatcher.serviceChanged( new ComponentChangedEvent( this, AbstractServiceContext.ServiceChange.CHILD_ADDED ));
	}

	@Override
	public void removeChild( IJxseComponent<?,?> child ){
		this.children.remove( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, AbstractServiceContext.ServiceChange.CHILD_REMOVED ));
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
	protected boolean validateComponent( IComponentFactory<?> factory, IJxseComponent<?,?> component ){
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
	public static IJxseComponent<?,?> addModule( AbstractServiceContext context, Object module ){
		IJxseComponent<Object,?> component = null;
		if( module instanceof IJxseComponent )
			component = (IJxseComponent<Object,?>) module;
		else
			component = new JxseComponent( context, module );

		//TODO CP:component.putProperty( ModuleProperties.CREATE_DATE, Calendar.getInstance().getTime() );
		if( module instanceof NetworkManager ){
			IJxseComponentNode<Object> node = new JxseComponentNode( context, component );
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

	protected static void removeModule( AbstractServiceContext context, Object module ){
		Collection<IJxseComponent<?,?>> temp = new ArrayList<IJxseComponent<?,?>>( context.getChildren() );
		for( IJxseComponent<?,?> component: temp ){
			if( component.getModule().equals( module ))
				context.getChildren().remove(component);
		}
	}

	@Override
	public Iterator<IJxseProperties> iterator(){
		return this.properties.propertyIterator();
	}

	@Override
	public NetworkManager getModule() {
		return this.networkManager;
	}

	/**
	 * Get the startup service 
	 * @param context
	 * @return
	 */
	public static FactoryNode<?> getChild( Components component, FactoryNode<JxseServiceContext> context ){
		for( ComponentNode<?> node: context.getChildren() ){
			if( ((IComponentFactory<?>) node.getData()).getComponentId().equals( component )){
				return (FactoryNode<?>) node;
			}
		}
		return null;
	}

}