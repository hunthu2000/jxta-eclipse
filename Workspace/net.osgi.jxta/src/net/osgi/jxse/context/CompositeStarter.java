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

import net.osgi.jxse.activator.IActivator;
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxseDirectives.Types;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class CompositeStarter<T extends Object, U extends Enum<U>, V extends IJxseDirectives> {

	private Collection<ICompositeBuilderListener<?>> factoryListeners;
	private ComponentNode<T,U,V> root;
	private boolean completed;
	
	public CompositeStarter( ComponentNode<T,U,V> root ) {
		this.root = root;
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
	@SuppressWarnings({"rawtypes", "unchecked" })
	private synchronized final ComponentNode parseFactories( ComponentNode<?, ?, V> node ){
		IComponentFactory<?,?,?> factory = node.getFactory();
		if( factory == null ){
			this.completed = false;
			return null;
		}
		
		Object module = null;
		if(!factory.isCompleted() ){
			this.parseDirectives( node );
			module = factory.createModule();
		}
		for( ComponentNode<?,?,?> child: node.getChildren())
			parseFactories( (ComponentNode<?, ?, V>) child );
		if( factory.isCompleted() )
			return node;
		if( module == null ){
			this.completed = false;
			return null;
		}
		factory.complete();
		this.notifyListeners( new ComponentBuilderEvent( this, factory, BuilderEvents.COMPONENT_CREATED ));
		IJxsePropertySource<?,V> ps = (IJxsePropertySource<?, V>) factory.getPropertySource();
		Iterator<?> iterator = ps.directiveIterator();
		V directive;
		while( iterator.hasNext()) {
			directive = (V)iterator.next();
			this.onParseDirectiveAfterCreation( node, ( V )directive, ( String )ps.getDirective( directive ));
		}

		return node;
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
	
	public synchronized void start() {
		this.completed = true;
		this.parseFactories( this.root);
	}

	protected void onParseDirectivePriorToCreation( ComponentNode<?,?,?> node, IJxseDirectives directive, String value) {
		if( node.getParent() == null )
			return;
		IComponentFactory<?,?,?> parentFactory = node.getParent().getFactory();
		if(!(directive instanceof Directives ))
			return;
		Directives dir = ( Directives )directive;
		switch( dir ){
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
	protected void onParseDirectiveAfterCreation( ComponentNode<?,?,?> node, IJxseDirectives directive, String value) {
		if( node == null )
			return;
		IComponentFactory<?,?,?> factory = node.getFactory();
		if(( !factory.isCompleted() ) || !(directive instanceof Directives ))
			return;
		Directives dir = ( Directives )directive;
		switch( dir ){
		case CONTEXT:
			if( Utils.isNull( value ))
				break;
			Types type = Types.valueOf( StringStyler.styleToEnum(value));
			switch( type ){
			case CHAUPAL:
				break;
			default:
				break;
			}
			
			break;
		case AUTO_START:
				boolean ap = Boolean.parseBoolean( value );
				if( !ap )
					break;
				Object pc = factory.getModule();
				if(!( pc instanceof IActivator ))
					return;
				IActivator activator = ( IActivator )pc;
				activator.start();
				break;
			default:
				break;
		}
	}

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	@SuppressWarnings("unchecked")
	private final void parseDirectives( ComponentNode<?,?,V> node ){
		IJxsePropertySource<?,V> propertySource = this.root.getFactory().getPropertySource();
		if( node.getFactory() != null )
			propertySource = node.getFactory().getPropertySource();
		Iterator<?> iterator = propertySource.directiveIterator();
		V directive;
		while( iterator.hasNext()) {
			directive = (V)iterator.next();
			this.onParseDirectivePriorToCreation( node, ( V )directive, ( String )propertySource.getDirective( directive ));
		}
	}
}
