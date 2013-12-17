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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxsePropertySource;

public class CompositeStarter<T extends Object, U extends Object> {

	private Collection<ICompositeBuilderListener<?>> factoryListeners;
	private ComponentNode<T,U> root;
	private boolean completed;
	private List<ComponentNode<?,?>> nodes;
	
	public CompositeStarter( ComponentNode<T,U> root ) {
		this.root = root;
		nodes = new ArrayList<ComponentNode<?,?>>();
		this.factoryListeners = new ArrayList<ICompositeBuilderListener<?>>();
	}

	public boolean isCompleted() {
		return completed;
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#addListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	public void addListener( ICompositeBuilderListener<?> listener ){
		this.factoryListeners.add( listener);
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#removeListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	public void removeListener( ICompositeBuilderListener<?> listener ){
		this.factoryListeners.remove( listener);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void notifyListeners( ComponentBuilderEvent<?> event ){
		for( ICompositeBuilderListener listener: factoryListeners )
			listener.notifyCreated(event);
	}
 
	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private synchronized final void parseFactories( ComponentNode<?, ?> node ){
		IComponentFactory<?,?> factory = node.getFactory();
		if( factory == null ){
			this.completed = false;
			return;
		}
		nodes.add( node );
		if( factory instanceof ICompositeBuilderListener )
			this.addListener((ICompositeBuilderListener<?>) factory);
		for( ComponentNode<?,?> child: node.getChildren())
			parseFactories( (ComponentNode<?, ?>) child );
		factory.complete();
	}
	
	/**
	 * Create the module
	 * @param node
	 * @return
	 */
	private Object createModule( ComponentNode<?, ?> node ){
		IComponentFactory<?,?> factory = node.getFactory();
		Object module = null;
		if(factory.isCompleted() )
			return factory.getModule();
		
		try{
			module = factory.createModule();
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		boolean auto_start = Boolean.parseBoolean( (String) factory.getPropertySource().getDirective( Directives.AUTO_START ));
		if(!auto_start ){
			return module;
		}
		for( ComponentNode<?,?> child: node.getChildren())
			parseFactories( (ComponentNode<?, ?>) child );
		if( module == null )
			return null;
		
		this.notifyListeners( new ComponentBuilderEvent<ComponentNode<?,?>>( this, node, BuilderEvents.COMPONENT_CREATED ));
		factory.complete();
		return module;
	}
	
	/**
	 * Try to start the modules
	 */
	private synchronized void startModules(){
		while( this.nodes.size() > 0 ){
			ComponentNode<?,?> node;
			for( int i=0; i<nodes.size(); i++ ){
				node = this.nodes.get(i);
				if( node.getFactory().moduleActive() ){
					this.nodes.remove(node);
					if( node.getFactory() instanceof ICompositeBuilderListener )
						this.removeListener((ICompositeBuilderListener<?>) node.getFactory());
				}
				else
					this.createModule(node);
			}
		}
		this.completed = true;
	}
	
	public synchronized void start() {
		this.completed = true;
		this.parseFactories( this.root);
		this.startModules();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ComponentNode<?,?> getNode( ComponentNode<?,?> node, IComponentFactory<?,?> factory ){
		ComponentNode<?,?> childNode = null;
		if( node == null )
			childNode = new ComponentNode( factory );
		else
			childNode = node.addChild( factory );
		return childNode;
	}
	

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectives( ComponentNode<?,?> node ){
		IJxsePropertySource<?> propertySource = this.root.getFactory().getPropertySource();
		if( node.getFactory() != null )
			propertySource = node.getFactory().getPropertySource();
		Iterator<IJxseDirectives> iterator = propertySource.directiveIterator();
		IJxseDirectives directive;
		while( iterator.hasNext()) {
			directive = iterator.next();
			//this.onParseDirectivePriorToCreation( node, directive, ( String )propertySource.getDirective( directive ));
		}
	}
}
