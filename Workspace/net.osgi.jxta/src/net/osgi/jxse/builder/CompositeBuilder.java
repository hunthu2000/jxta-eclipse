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

import net.osgi.jxse.builder.ICompositeBuilderListener.FactoryEvents;
import net.osgi.jxse.context.JxseContextPropertySource;
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
import net.osgi.jxse.peergroup.PeerGroupFactory;
import net.osgi.jxse.peergroup.PeerGroupPropertySource;
import net.osgi.jxse.properties.AbstractPeerGroupProviderPropertySource.PeerGroupDirectives;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.registration.RegistrationPropertySource;
import net.osgi.jxse.registration.RegistrationServiceFactory;
import net.osgi.jxse.seeds.ISeedListFactory;
import net.osgi.jxse.seeds.SeedListFactory;
import net.osgi.jxse.seeds.SeedListPropertySource;
import net.osgi.jxse.utils.Utils;

public class CompositeBuilder<T extends Object, U extends Enum<U>, V extends IJxseDirectives> implements ICompositeBuilder<T,U,V> {

	private Collection<ICompositeBuilderListener> factoryListeners;
	private ComponentNode<T,U,V> root;
	private IJxsePropertySource<U,V> ps;

	
	public CompositeBuilder( IJxsePropertySource<U,V> propertySource) {
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

	private void notifyListeners( ComponentFactoryEvent event ){
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final void parsePropertySources(){
		this.root = (ComponentNode<T, U, V>) this.parsePropertySources(ps, new ComponentNode( null ));
	}

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	@SuppressWarnings({"rawtypes", "unchecked" })
	private final ComponentNode parsePropertySources( IJxsePropertySource<?,?> source, ComponentNode<?, ?, ?> parent ){
		ComponentNode node = null;
		ComponentNode returnNode = null;
		IComponentFactory<?,?,?> factory = parent.getFactory();
		boolean newFactory = false;
		if( source instanceof JxseContextPropertySource ){
			node = parent;
		}
		if( source instanceof NetworkManagerPropertySource ){
			factory = new NetworkManagerFactory( (IJxsePropertySource<NetworkManagerProperties, IJxseDirectives>) source );
			node = this.getNode(parent, factory);
			returnNode = node;
			newFactory = true;
		}else if( source instanceof NetworkConfigurationPropertySource ){
			factory = new NetworkConfigurationFactory( (NetworkManagerFactory) parent.getFactory(), (NetworkConfigurationPropertySource) source );
			node = this.getNode(parent, factory);
			newFactory = true;
		}else if( source instanceof SeedListPropertySource ){
			NetworkConfigurationFactory ncf = (NetworkConfigurationFactory) parent.getFactory();
			ISeedListFactory slf = new SeedListFactory((SeedListPropertySource<IJxseDirectives>) source ); 
			ncf.addSeedlist(slf);
		}else if( source instanceof DiscoveryPropertySource ){
			DiscoveryPropertySource discsource = ( DiscoveryPropertySource )source;
			String peergroup = (String) discsource.getDirective( PeerGroupDirectives.PEERGROUP );
			ComponentNode<IPeerGroupProvider,?,?> provider = this.getPeerGroupProvider( peergroup, parent);
			factory = new DiscoveryServiceFactory( (IPeerGroupProvider) provider.getFactory(), (DiscoveryPropertySource)source );
			node = this.getNode(provider, factory);
			newFactory = true;
		}else if( source instanceof PeerGroupPropertySource ){
			PeerGroupPropertySource peersource = ( PeerGroupPropertySource )source;
			String peergroup = (String)peersource.getDirective( PeerGroupDirectives.PEERGROUP );
			ComponentNode<IPeerGroupProvider,?,?>  provider = this.getPeerGroupProvider( peergroup, parent);
			factory = new PeerGroupFactory( (IPeerGroupProvider) provider.getFactory(), (PeerGroupPropertySource)source );
			node = this.getNode(parent, factory);
			newFactory = true;
		}else if( source instanceof RegistrationPropertySource ){
			RegistrationPropertySource rescsource = ( RegistrationPropertySource )source;
			String peergroup = (String) rescsource.getDirective( PeerGroupDirectives.PEERGROUP );
			ComponentNode<IPeerGroupProvider,?,?> provider = this.getPeerGroupProvider( peergroup, parent);
			factory = new RegistrationServiceFactory( (IPeerGroupProvider) provider.getFactory(), rescsource );
			node = this.getNode(parent, factory);
			newFactory = true;
		}
		this.onProperytySourceCreated(source);
		ComponentNode childNode = null;
		for( IJxsePropertySource<?,?> child: source.getChildren()) {
			childNode = this.parsePropertySources( child, node );
			if(( returnNode == null ) && ( childNode != null ))
				returnNode = childNode;
		}
		if( newFactory )
			this.notifyListeners( new ComponentFactoryEvent( this, factory, FactoryEvents.FACTORY_CREATED ));
		return returnNode;
	}

	/**
	 * returns the node that contains a peergroup provider
	 * @param name
	 * @param parent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private final ComponentNode<IPeerGroupProvider,?,?> getPeerGroupProvider( String name, ComponentNode<?, ?, ?> parent ){
		IComponentFactory<?,?,?> factory = parent.getFactory();
		if( Utils.isNull( name )){
			if( factory instanceof IPeerGroupProvider )
				return (ComponentNode<IPeerGroupProvider, ?, ?>) parent;
			name = IPeerGroupProvider.S_NET_PEER_GROUP;
		}else{
			if( factory instanceof IPeerGroupProvider ){
				IPeerGroupProvider provider = (IPeerGroupProvider) factory;
				if( provider.getPeerGroupName().equals(name ))
					return (ComponentNode<IPeerGroupProvider, ?, ?>) parent;
			}
		}
		ComponentNode<IPeerGroupProvider,?,?> returnNode= null;
		for( ComponentNode<?,?,?> child: parent.getChildren()) {
			returnNode = this.getPeerGroupProvider( name, child );
			if( returnNode != null )
				return returnNode;
		}
		return returnNode;
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
	public ComponentNode<T,U,V> build() {
		this.parsePropertySources();
		return this.root;
	}
}
