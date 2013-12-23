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
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.FactoryNode;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxsePropertySource;

public class CompositeStarter<T extends Object> {

	private Collection<ICompositeBuilderListener<?>> factoryListeners;
	private FactoryNode<T> root;
	private boolean completed;
	private List<ComponentNode<?>> nodes;
	
	public CompositeStarter( FactoryNode<T> root ) {
		this.root = root;
		nodes = new ArrayList<ComponentNode<?>>();
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
	private synchronized final void parseFactories( FactoryNode<?> node ){
		IComponentFactory<?> factory = node.getData();
		if( factory == null ){
			this.completed = false;
			return;
		}
		nodes.add( node );
		if( factory instanceof ICompositeBuilderListener )
			this.addListener((ICompositeBuilderListener<?>) factory);
		for( ComponentNode<?> child: node.getChildren())
			parseFactories( (FactoryNode<?>) child );
		factory.complete();
	}
	
	/**
	 * Create the module
	 * @param node
	 * @return
	 */
	private Object createModule( FactoryNode<?> node ){
		IComponentFactory<?> factory = node.getData();
		Object module = null;
		if(factory.isCompleted() )
			return factory.getComponent();
		
		try{
			module = factory.createComponent();
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		boolean auto_start = Boolean.parseBoolean( (String) factory.getPropertySource().getDirective( Directives.AUTO_START ));
		if(!auto_start ){
			return module;
		}
		for( ComponentNode<?> child: node.getChildren())
			parseFactories( (FactoryNode<?>) child );
		if( module == null )
			return null;
		
		this.notifyListeners( new ComponentBuilderEvent<ComponentNode<?>>( this, (IJxseModule<ComponentNode<?>>) node, BuilderEvents.COMPONENT_CREATED ));
		factory.complete();
		return module;
	}
	
	/**
	 * Try to start the modules
	 */
	private synchronized void startModules(){
		while( this.nodes.size() > 0 ){
			FactoryNode<?> node;
			for( int i=0; i<nodes.size(); i++ ){
				node = (FactoryNode<?>) this.nodes.get(i);
				if( node.getData().moduleActive() ){
					this.nodes.remove(node);
					if( node.getData() instanceof ICompositeBuilderListener )
						this.removeListener((ICompositeBuilderListener<?>) node.getData());
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
	protected ComponentNode<?> getNode( ComponentNode<?> node, IComponentFactory<?> factory ){
		FactoryNode<?> childNode = null;
		if( node == null )
			childNode = new FactoryNode( factory );
		else
			childNode = ( FactoryNode )node.addChild( factory );
		return childNode;
	}
	

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectives( FactoryNode<?> node ){
		IJxsePropertySource<?> propertySource = this.root.getData().getPropertySource();
		if( node.getData() != null )
			propertySource = node.getData().getPropertySource();
		Iterator<IJxseDirectives> iterator = propertySource.directiveIterator();
		IJxseDirectives directive;
		while( iterator.hasNext()) {
			directive = iterator.next();
			//this.onParseDirectivePriorToCreation( node, directive, ( String )propertySource.getDirective( directive ));
		}
	}
}
