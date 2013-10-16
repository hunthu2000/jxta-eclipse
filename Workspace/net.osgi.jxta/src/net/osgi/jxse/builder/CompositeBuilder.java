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
package net.osgi.jxse.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.osgi.jxse.activator.IActivator;
import net.osgi.jxse.builder.ICompositeBuilderListener.FactoryEvents;
import net.osgi.jxse.discovery.DiscoveryPropertySource;
import net.osgi.jxse.discovery.DiscoveryServiceFactory;
import net.osgi.jxse.factory.ComponentFactoryEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.network.NetworkConfigurationFactory;
import net.osgi.jxse.network.NetworkConfigurationPropertySource;
import net.osgi.jxse.network.NetworkManagerFactory;
import net.osgi.jxse.network.NetworkManagerPropertySource;
import net.osgi.jxse.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.peergroup.IPeerGroupProvider.PeerGroupDirectives;
import net.osgi.jxse.peergroup.PeerGroupPropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.SeedListPropertySource;
import net.osgi.jxse.seeds.ISeedListFactory;
import net.osgi.jxse.seeds.SeedListFactory;
import net.osgi.jxse.utils.Utils;

public class CompositeBuilder<T extends Object, U extends Enum<U>> implements ICompositeBuilder<T> {

	private Collection<ICompositeBuilderListener> factoryListeners;
	private ComponentNode<T,U,IJxseDirectives> root;
	private IJxsePropertySource<U,IJxseDirectives> ps;

	
	public CompositeBuilder( IJxsePropertySource<U, IJxseDirectives> propertySource) {
		ps = propertySource;
		this.factoryListeners = new ArrayList<ICompositeBuilderListener>();
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#addListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	@Override
	public void addListener( ICompositeBuilderListener listener ){
		this.factoryListeners.add( listener);
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#removeListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	@Override
	public void removeListener( ICompositeBuilderListener listener ){
		this.factoryListeners.remove( listener);
	}

	protected void notifyListeners( ComponentFactoryEvent event ){
		for( ICompositeBuilderListener listener: factoryListeners )
			listener.notifyCreated(event);
	}
 
	/**
	 * Do nothing
	 */
	protected void onProperytySourceCreated( IJxsePropertySource<?,?> ps ) {}

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	@SuppressWarnings({ "unchecked" })
	private final void parsePropertySources(){
		this.root = (ComponentNode<T, U, IJxseDirectives>) this.parsePropertySources(ps, new ComponentNode<T, U, IJxseDirectives>( null ));
	}

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	@SuppressWarnings({"rawtypes", "unchecked" })
	private final ComponentNode parsePropertySources( IJxsePropertySource<?,IJxseDirectives> source, ComponentNode<?, ?, IJxseDirectives> parent ){
		if(( source == null ) || ( parent == null ))
			return null;
		ComponentNode node = parent;
		ComponentNode returnNode = null;
		boolean newFactory = false;
		IComponentFactory<?,?,IJxseDirectives> factory = parent.getFactory();
		if( source instanceof NetworkManagerPropertySource ){
			factory = new NetworkManagerFactory( (IJxsePropertySource<NetworkManagerProperties, IJxseDirectives>) source );
			node = this.getNode(parent, factory);
			returnNode = node;
			newFactory = true;
		}else if( source instanceof NetworkConfigurationPropertySource ){
			factory = new NetworkConfigurationFactory( (NetworkManagerFactory) factory, (NetworkConfigurationPropertySource) source );
			node = this.getNode(parent, factory);
			newFactory = true;
		}else if( source instanceof SeedListPropertySource ){
			NetworkConfigurationFactory ncf = (NetworkConfigurationFactory) parent.getFactory();
			ISeedListFactory slf = new SeedListFactory((SeedListPropertySource<IJxseDirectives>) source ); 
			ncf.addSeedlist(slf);
		}else if( source instanceof DiscoveryPropertySource ){
			String peergroup = (String) source.getDirective( PeerGroupDirectives.PEERGROUP );
			if( Utils.isNull( peergroup ))
					peergroup = PeerGroupPropertySource.S_NET_PEERGROUP;
			
			IPeerGroupProvider provider = this.getPeerGroupProvider( peergroup, parent);
			factory = new DiscoveryServiceFactory( provider, (DiscoveryPropertySource)source );
			node = this.getNode(parent, factory);
			newFactory = true;
		}
		this.onProperytySourceCreated(source);
		ComponentNode childNode = null;
		for( IJxsePropertySource child: source.getChildren()) {
			childNode = this.parsePropertySources( child, node );
			if(( returnNode == null ) &&( childNode != null ))
				returnNode = childNode;
		}
		if( newFactory )
			this.notifyListeners( new ComponentFactoryEvent( this, factory, FactoryEvents.FACTORY_CREATED ));
		return returnNode;
	}

	/**
	 * Look for a peergroup provider with the given name
	 * @param name
	 * @param parent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private IPeerGroupProvider getPeerGroupProvider( String name, ComponentNode<?, ?, IJxseDirectives> parent  ){
		if( Utils.isNull( name ))
			return null;
		IComponentFactory<?,?,IJxseDirectives> factory = parent.getFactory();
		if(( factory != null ) && ( factory instanceof IPeerGroupProvider )){
			IPeerGroupProvider provider = ( IPeerGroupProvider )factory;
			if( name.equals( provider.getPeerGroupProviderName() ))
				return provider;
		}
		for( ComponentNode<?, ?, ?> child: parent.getChildren() ) {
			IPeerGroupProvider childProvider = this.getPeerGroupProvider(name, (ComponentNode<?, ?, IJxseDirectives>) child );
			if( childProvider != null )
				return childProvider;
		}	
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ComponentNode<?,?,?> getNode( ComponentNode<?,?,?> node, IComponentFactory<?,?,?> factory ){
		ComponentNode<?,?,?> childNode = null;
		if( node == null )
			childNode = new ComponentNode( factory );
		else
			childNode = node.addChild( factory );
		return childNode;
	}
	
	@Override
	public T build() {
		this.parsePropertySources();
		return this.createModule( this.root);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private T createModule( ComponentNode node ){
		IComponentFactory factory = node.getFactory();
		T component = null;
		if( factory != null ){
			this.parseDirectives( node );
			component = (T) factory.createModule();
			this.notifyListeners( new ComponentFactoryEvent( this, factory, FactoryEvents.COMPONENT_CREATED ));
		}
		for( ComponentNode<?,?,?> child: node.getChildren())
			createModule( child );
		factory.complete();
		return component;
		
	}

	protected void onParseDirectivePriorToCreation( ComponentNode<?,?,?> node, IJxseDirectives.Directives directive, String value) {
		if( node.getParent() == null )
			return;
		IComponentFactory<?,?,?> parentFactory = node.getParent().getFactory();
		switch( directive ){
			case ACTIVATE_PARENT:
				boolean ap = Boolean.parseBoolean( value );
				if( !ap || !parentFactory.isCompleted())
					break;
				Object pc = parentFactory.getModule();
				if(!( pc instanceof IActivator ))
					return;
				IActivator activator = ( IActivator )pc;
				activator.start();
				break;
			case CREATE_PARENT:
				boolean cp = Boolean.parseBoolean( value );
				if( cp && !parentFactory.isCompleted())
					parentFactory.createModule();
				break;
			default:
				break;
		}
	}

	/**
	 * Do nothing
	 */
	protected void onParseDirectiveAfterCreation( ComponentNode<?,?,?> node, IJxseDirectives.Directives directive, Object value) {}

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectives( ComponentNode<?,?,IJxseDirectives> node ){
		IJxsePropertySource<?,IJxseDirectives> propertySource = this.ps;
		if( node.getFactory() != null )
			propertySource = node.getFactory().getPropertySource();
		Iterator<IJxseDirectives> iterator = propertySource.directiveIterator();
		IJxseDirectives directive;
		while( iterator.hasNext()) {
			directive = iterator.next();
			this.onParseDirectivePriorToCreation( node, ( IJxseDirectives.Directives )directive, ( String )propertySource.getDirective( directive ));
		}
	}
}
