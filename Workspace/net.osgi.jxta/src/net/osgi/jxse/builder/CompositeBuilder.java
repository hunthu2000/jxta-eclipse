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
import net.osgi.jxse.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jxse.component.ModuleNode;
import net.osgi.jxse.context.ContextModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.FactoryNode;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.utils.Utils;

public class CompositeBuilder implements ICompositeBuilder<ModuleNode<ContextModule>> {

	private Collection<ICompositeBuilderListener<?>> builderlisteners;
	private ModuleNode<NetworkManager> networkRoot;
	private ModuleNode<ContextModule> root;
	
	public CompositeBuilder( ModuleNode<ContextModule> root ) {
		this.root = root;
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

	@Override
	public ModuleNode<ContextModule> build() {
		this.extendPropertySources(root);
		this.parseModules(this.root, null );
		return this.root;
	}

	/**
	 * Extend the property sources, if this is required.
	 * @param node
	 */
	private final void extendPropertySources( ModuleNode<?> node){
		node.getData().extendModules();	
		for( ComponentNode<?> child: node.getChildren()) {
			this.extendPropertySources((ModuleNode<?>) child );
		}
	}

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	@SuppressWarnings({"rawtypes", "unchecked" })
	private final void parseModules( ModuleNode<?> node, ModuleNode<?> parent ){
		if( !node.getData().canCreate() )
			return;
		node.getData().createFactory();	
		for( ComponentNode<?> child: node.getChildren()) {
			this.parseModules( (ModuleNode<?>) child, node );
		}
		this.notifyListeners( new ComponentBuilderEvent( this, node.getData(), BuilderEvents.FACTORY_CREATED ));
	}

	/**
	 * returns the node that contains a peergroup provider
	 * @param name
	 * @param parent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private final ModuleNode<IPeerGroupProvider> getPeerGroupProvider( String name, ModuleNode<?> parent ){
		String providerName = IPeerGroupProvider.S_NET_PEER_GROUP;
		if( !Utils.isNull( name ))
			providerName = name;
		
		ModuleNode<IPeerGroupProvider> returnNode= (ModuleNode<IPeerGroupProvider>) findFirstPeerGroupProviderParent(parent, providerName);
		if( returnNode != null )
			return returnNode;
		
		for( ComponentNode<?> child: this.networkRoot.getChildren()) {
			returnNode = this.getPeerGroupProvider( providerName, (ModuleNode<?>) child );
			if( returnNode != null )
				return returnNode;
		}
		
		return returnNode;
	}

	private ModuleNode<?> findFirstPeerGroupProviderParent( ModuleNode<?> parent, String name ){
		if( parent == null )
			return null;
		IComponentFactory<IJxseModule<?>> module = (IComponentFactory<IJxseModule<?>>) parent.getData();
		if( module instanceof IPeerGroupProvider )
			return parent;
		 return findFirstPeerGroupProviderParent( (ModuleNode<?>) parent.getParent(), name );
	}
	
	/**
	 * returns the node that contains a peergroup provider
	 * @param name
	 * @param parent
	 * @return
	 */
	protected final ModuleNode<IPeerGroupProvider> getPeerGroupProviderNotNull( String name, ModuleNode<?> parent ){
		ModuleNode<IPeerGroupProvider> provider = getPeerGroupProvider( name, parent );
		if( provider == null )
			provider = getPeerGroupProvider( IPeerGroupProvider.S_NET_PEER_GROUP, parent );
		return provider;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected FactoryNode<?> createNode( ComponentNode<?> node, IComponentFactory<?> factory ){
		FactoryNode<?> childNode = null;
		if( node == null )
			childNode = new FactoryNode( factory );
		else
			childNode = (FactoryNode )node.addChild( factory );
		return childNode;
	}
}