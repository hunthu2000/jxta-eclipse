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

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.activator.JxseStartupPropertySource;
import net.osgi.jxse.activator.StartupServiceFactory;
import net.osgi.jxse.advertisement.AdvertisementPropertySource;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.osgi.jxse.advertisement.JxseAdvertisementFactory;
import net.osgi.jxse.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jxse.context.ContextFactory;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.context.JxseServiceContext;
import net.osgi.jxse.discovery.DiscoveryPropertySource;
import net.osgi.jxse.discovery.DiscoveryServiceFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
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
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.registration.RegistrationPropertySource;
import net.osgi.jxse.registration.RegistrationServiceFactory;
import net.osgi.jxse.seeds.ISeedListFactory;
import net.osgi.jxse.seeds.SeedListFactory;
import net.osgi.jxse.seeds.SeedListPropertySource;
import net.osgi.jxse.utils.Utils;

public class CompositeBuilder<T extends Object, U extends Object, V extends IJxseDirectives> implements ICompositeBuilder<T,U,V> {

	private Collection<ICompositeBuilderListener<?>> builderlisteners;
	private ComponentNode<T,U,V> root;
	private ComponentNode<NetworkManager,NetworkManagerProperties,IJxseDirectives> networkRoot;
	private IJxsePropertySource<U,V> ps;

	
	public CompositeBuilder( IJxsePropertySource<U,V> propertySource) {
		ps = propertySource;
		this.builderlisteners = new ArrayList<ICompositeBuilderListener<?>>();
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#addListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	@Override
	public void addListener( ICompositeBuilderListener<?> listener ){
		this.builderlisteners.add( listener);
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#removeListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	@Override
	public void removeListener( ICompositeBuilderListener<?> listener ){
		this.builderlisteners.remove( listener);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void notifyListeners( ComponentBuilderEvent event ){
		for( ICompositeBuilderListener<?> listener: builderlisteners )
			listener.notifyCreated(event);
	}
 
	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	@SuppressWarnings({ "unchecked" })
	private final void parsePropertySources(){
		this.root = this.parsePropertySources(ps, null );
	}

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	@SuppressWarnings({"rawtypes", "unchecked" })
	private final ComponentNode parsePropertySources( IJxsePropertySource<?,?> source, ComponentNode<?, ?, ?> parent ){
		ComponentNode node = null;
		IComponentFactory<?,?,?> factory = null;
		if( source instanceof JxseContextPropertySource ){
			factory = new ContextFactory( (JxseContextPropertySource) source );
		}else if( source instanceof JxseStartupPropertySource ){
			factory = new StartupServiceFactory( (ComponentNode<JxseServiceContext, IJxseProperties, IJxseDirectives>) this.root, (JxseStartupPropertySource) source );
		}else if( source instanceof NetworkManagerPropertySource ){
			factory = new NetworkManagerFactory( (IJxsePropertySource<NetworkManagerProperties, IJxseDirectives>) source );
			this.networkRoot = node;
		}else if( source instanceof NetworkConfigurationPropertySource ){
			factory = new NetworkConfigurationFactory( (NetworkManagerFactory) parent.getFactory(), (NetworkConfigurationPropertySource) source );
		}else if( source instanceof SeedListPropertySource ){
			NetworkConfigurationFactory ncf = (NetworkConfigurationFactory) parent.getFactory();
			ISeedListFactory slf = new SeedListFactory((SeedListPropertySource<IJxseDirectives>) source ); 
			ncf.addSeedlist(slf);
		}else if( source instanceof DiscoveryPropertySource ){
			DiscoveryPropertySource discsource = ( DiscoveryPropertySource )source;
			String peergroup = (String) discsource.getDirective( PeerGroupDirectives.PEERGROUP );
			ComponentNode<IPeerGroupProvider,?,?> provider = this.getPeerGroupProviderNotNull( peergroup, parent);
			factory = new DiscoveryServiceFactory( (IPeerGroupProvider) provider.getFactory(), (DiscoveryPropertySource)source );
		}else if( source instanceof AdvertisementPropertySource ){
			IJxsePropertySource<IJxseProperties, IJxseDirectives> advsource = 
					(IJxsePropertySource<IJxseProperties, IJxseDirectives>) source;
			String peergroup = (String) advsource.getDirective( AdvertisementDirectives.PEERGROUP );
			ComponentNode<IPeerGroupProvider,?,?> provider = this.getPeerGroupProviderNotNull( peergroup, parent);
			factory = new JxseAdvertisementFactory( (IPeerGroupProvider) provider.getFactory(), advsource );
		}else if( source instanceof PeerGroupPropertySource ){
			PeerGroupPropertySource peersource = ( PeerGroupPropertySource )source;
			String peergroup = (String)peersource.getDirective( PeerGroupDirectives.PEERGROUP );
			ComponentNode<IPeerGroupProvider,?,?>  provider = this.getPeerGroupProviderNotNull( peergroup, parent);
			factory = new PeerGroupFactory( (IPeerGroupProvider) provider.getFactory(), (PeerGroupPropertySource)source );
		}else if( source instanceof RegistrationPropertySource ){
			RegistrationPropertySource rescsource = ( RegistrationPropertySource )source;
			String peergroup = (String) rescsource.getDirective( PeerGroupDirectives.PEERGROUP );
			ComponentNode<IPeerGroupProvider,?,?> provider = this.getPeerGroupProviderNotNull( peergroup, parent);
			factory = new RegistrationServiceFactory( (IPeerGroupProvider) provider.getFactory(), rescsource );
		}

		if( factory != null ){
			node = this.createNode(parent, factory);
			if( source instanceof JxseContextPropertySource ){			
				this.root = node;
			}
		}
		
		for( IJxsePropertySource<?,?> child: source.getChildren()) {
			this.parsePropertySources( child, node );
		}
		this.notifyListeners( new ComponentBuilderEvent( this, factory, BuilderEvents.FACTORY_CREATED ));
		return this.root;
	}

	/**
	 * returns the node that contains a peergroup provider
	 * @param name
	 * @param parent
	 * @return
	 */
	private final ComponentNode<IPeerGroupProvider,?,?> getPeerGroupProvider( String name, ComponentNode<?, ?, ?> parent ){
		String providerName = IPeerGroupProvider.S_NET_PEER_GROUP;
		if( !Utils.isNull( name ))
			providerName = name;
		
		ComponentNode<IPeerGroupProvider,?,?> returnNode= findFirstPeerGroupProviderParent(parent, providerName);
		if( returnNode != null )
			return returnNode;
		
		for( ComponentNode<?,?,?> child: this.networkRoot.getChildren()) {
			returnNode = this.getPeerGroupProvider( providerName, child );
			if( returnNode != null )
				return returnNode;
		}
		
		return returnNode;
	}

	@SuppressWarnings("unchecked")
	private ComponentNode<IPeerGroupProvider, ?, ?> findFirstPeerGroupProviderParent( ComponentNode<?, ?, ?> parent, String name ){
		if( parent == null )
			return null;
		IComponentFactory<?,?,?> factory = parent.getFactory();
		if( factory instanceof IPeerGroupProvider )
			return (ComponentNode<IPeerGroupProvider, ?, ?>) parent;
		 return findFirstPeerGroupProviderParent( parent.getParent(), name );
	}
	
	/**
	 * returns the node that contains a peergroup provider
	 * @param name
	 * @param parent
	 * @return
	 */
	private final ComponentNode<IPeerGroupProvider,?,?> getPeerGroupProviderNotNull( String name, ComponentNode<?, ?, ?> parent ){
		ComponentNode<IPeerGroupProvider,?,?> provider = getPeerGroupProvider( name, parent );
		if( provider == null )
			provider = getPeerGroupProvider( IPeerGroupProvider.S_NET_PEER_GROUP, parent );
		return provider;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ComponentNode<?,?,?> createNode( ComponentNode<?,?,?> node, IComponentFactory<?,?,?> factory ){
		ComponentNode<?,?,?> childNode = null;
		if( node == null )
			childNode = new ComponentNode( factory );
		else
			childNode = node.addChild( factory );
		return childNode;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T build() {
		this.parsePropertySources();
		return (T) this.root;
	}
}
