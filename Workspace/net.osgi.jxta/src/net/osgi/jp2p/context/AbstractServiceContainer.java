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
package net.osgi.jp2p.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.osgi.jp2p.activator.AbstractActivator;
import net.osgi.jp2p.activator.IActivator;
import net.osgi.jp2p.component.ComponentChangedEvent;
import net.osgi.jp2p.component.ComponentEventDispatcher;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.component.IJp2pComponentNode;
import net.osgi.jp2p.component.Jp2pComponent;
import net.osgi.jp2p.component.Jp2pComponentNode;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.properties.AbstractJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;
import net.osgi.jp2p.utils.StringStyler;

public class AbstractServiceContainer extends AbstractActivator 
implements	IJxseServiceContainer<NetworkManager>{

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
	private IJp2pPropertySource<IJp2pProperties> properties;
	
	private NetworkManager networkManager; 
	
	//Takes care of all the messaging through the container
	private ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
	
	private Swarm swarm;

	protected AbstractServiceContainer( String bundleId, String identifier) {
		this( (IJp2pPropertySource<IJp2pProperties>) new Jp2pContainerPropertySource( bundleId ));
	}

	protected AbstractServiceContainer( IJp2pPropertySource<IJp2pProperties> source ) {
		super();
		this.children = new ArrayList<IJp2pComponent<?>>();
		this.properties = source;
		swarm = new Swarm();
	}

	public IJp2pPropertySource<IJp2pProperties> getProperties() {
		return properties;
	}

	@Override
	public String getId() {
		return (String) this.properties.getDirective( Directives.ID );
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

	protected void setProperties(IJp2pWritePropertySource<IJp2pProperties> properties) {
		this.properties = properties;
	}

	@Override
	public Object getProperty(Object key) {
		return this.properties.getProperty( (IJp2pProperties) key );
	}

	protected void putProperty(Object key, Object value) {
		if( value == null )
			return;
		((AbstractJp2pWritePropertySource) this.properties).setProperty( (IJp2pProperties) key, value);
	}

	/**
	 * Get the category for the given key
	 * @param key
	 * @return
	 */
	public String getCategory( Object key ){
		return this.properties.getCategory( (IJp2pProperties) key );
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
	public IJp2pComponent<?> getParent() {
		return null;
	}
	
	@Override
	protected void deactivate() {
		NetworkManager manager = null;
		for( IJp2pComponent<?> component: this.children ){
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
	public Collection<IJp2pComponent<?>> getChildren(){
		return this.children;
	}

	@Override
	public void addChild( IJp2pComponent<?> child ){
		this.children.add( child );
		if( child.getModule() instanceof NetworkManager )
			this.networkManager = (NetworkManager) child.getModule();
		if( child.getModule() instanceof PeerGroup ){
			this.swarm.addPeerGroup(( PeerGroup )child.getModule());
		}
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

	/**
	 * add a module to the container. returns the JxseComponent, or null if something went wrong
	 * @param module
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static IJp2pComponent<?> addModule( AbstractServiceContainer context, Object module ){
		IJp2pComponent<Object> component = null;
		if( module instanceof IJp2pComponent )
			component = (IJp2pComponent<Object>) module;
		else
			component = new Jp2pComponent( context, module );

		//TODO CP:component.putProperty( ModuleProperties.CREATE_DATE, Calendar.getInstance().getTime() );
		if( module instanceof NetworkManager ){
			IJp2pComponentNode<Object> node = new Jp2pComponentNode( context, component );
			NetworkManager manager = ( NetworkManager )module;
			try {
				node.addChild( new Jp2pComponent( context, manager.getConfigurator() ));
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

	protected static void removeModule( AbstractServiceContainer context, Object module ){
		Collection<IJp2pComponent<?>> temp = new ArrayList<IJp2pComponent<?>>( context.getChildren() );
		for( IJp2pComponent<?> component: temp ){
			if( component.getModule().equals( module ))
				context.getChildren().remove(component);
		}
	}

	@Override
	public Iterator<IJp2pProperties> iterator(){
		return this.properties.propertyIterator();
	}

	@Override
	public NetworkManager getModule() {
		return this.networkManager;
	}
}