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
import net.osgi.jxse.factory.ComponentFactoryEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.network.NetworkConfigurationFactory;
import net.osgi.jxse.network.NetworkConfigurationPropertySource;
import net.osgi.jxse.network.NetworkManagerFactory;
import net.osgi.jxse.network.NetworkManagerPropertySource;
import net.osgi.jxse.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.SeedListPropertySource;
import net.osgi.jxse.seeds.ISeedListFactory;
import net.osgi.jxse.seeds.SeedListFactory;

public class CompositeBuilder<T extends Object, U extends Enum<U>, V extends IJxseDirectives> implements ICompositeBuilder<T> {

	private Collection<ICompositeBuilderListener> factoryListeners;
	private IComponentFactory<?,?,?> factory = null;
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
		this.root = (ComponentNode<T, U, V>) this.parsePropertySources(ps, null );
	}

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	@SuppressWarnings({"rawtypes", "unchecked" })
	private final ComponentNode parsePropertySources( IJxsePropertySource<?,?> source, ComponentNode<?, ?, ?> parent ){
		ComponentNode node = null;
		ComponentNode returnNode = null;
		boolean newFactory = false;
		if( source instanceof NetworkManagerPropertySource ){
			factory = new NetworkManagerFactory( (IJxsePropertySource<NetworkManagerProperties, Directives>) source );
			node = this.getNode(parent, factory);
			returnNode = node;
			newFactory = true;
		}else if( source instanceof NetworkConfigurationPropertySource ){
			factory = new NetworkConfigurationFactory( (NetworkManagerFactory) factory, (NetworkConfigurationPropertySource) source );
			node = this.getNode(parent, factory);
			newFactory = true;
		}else if( source instanceof SeedListPropertySource ){
			NetworkConfigurationFactory ncf = (NetworkConfigurationFactory) factory;
			ISeedListFactory slf = new SeedListFactory((SeedListPropertySource<IJxseDirectives>) source ); 
			ncf.addSeedlist(slf);
		}
		this.onProperytySourceCreated(source);
		ComponentNode childNode = null;
		for( IJxsePropertySource<?,?> child: source.getChildren()) {
			childNode = this.parsePropertySources( child, node );
			if(( returnNode == null ) &&( childNode != null ))
				returnNode = childNode;
		}
		if( newFactory )
			this.notifyListeners( new ComponentFactoryEvent( this, factory, FactoryEvents.FACTORY_CREATED ));
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
	private final void parseDirectives( ComponentNode<?,?,V> node ){
		IJxsePropertySource<?,V> propertySource = this.ps;
		if( node.getFactory() != null )
			propertySource = node.getFactory().getPropertySource();
		Iterator<V> iterator = propertySource.directiveIterator();
		V directive;
		while( iterator.hasNext()) {
			directive = iterator.next();
			this.onParseDirectivePriorToCreation( node, ( IJxseDirectives.Directives )directive, ( String )propertySource.getDirective( directive ));
		}
	}
}
