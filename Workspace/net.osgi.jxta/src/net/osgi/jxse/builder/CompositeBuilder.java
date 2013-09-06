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
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentFactoryEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.preferences.properties.IJxsePropertySource;

public class CompositeBuilder<T extends Object, U extends Enum<U>, V extends Enum<V>> extends AbstractComponentFactory<T,U,V> implements ICompositeBuilder<T> {

	private ComponentNode<T,U,V> node;
	
	private Collection<ICompositeBuilderListener> factoryListeners;
	
	public CompositeBuilder( ComponentNode<T,U,V> node) {
		super( node.getFactory().getPropertySource() );
		this.node = node;
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
			listener.notifyFactoryCreated(event);
	}
 
	@Override
	protected void onParseDirectivePriorToCreation( V directive, Object value) {
		this.onParseDirectivePriorToCreation( node, ( Directives )directive, ( String )value );
	}

	protected void onParseDirectivePriorToCreation( ComponentNode<T,U,V> node, Directives directive, String value) {
		IComponentFactory<?,?,?> parentFactory = null;
		switch( directive ){
			case ACTIVATE_PARENT:
				boolean ap = Boolean.parseBoolean( value );
				parentFactory = node.getParent().getFactory();
				if( !ap || !parentFactory.isCompleted())
					break;
				Object pc = node.getParent().getFactory().getModule();
				if(!( pc instanceof IActivator ))
					return;
				IActivator activator = ( IActivator )pc;
				activator.start();
				break;
			case CREATE_PARENT:
				boolean cp = Boolean.parseBoolean( value );
				parentFactory = node.getParent().getFactory();
				if( cp && !parentFactory.isCompleted())
					node.getParent().getFactory().createModule();
				break;
			default:
				break;
		}
	}

	/**
	 * Do nothing
	 */
	@Override
	protected void onParseDirectiveAfterCreation( T component, V directive, Object value) {}

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectives( ComponentNode<T,U,V> node ){
		IJxsePropertySource<U, V> ps = node.getFactory().getPropertySource();
		Iterator<V> iterator = ps.directiveIterator();
		V directive;
		while( iterator.hasNext()) {
			directive = iterator.next();
			this.onParseDirectivePriorToCreation( node, ( Directives )directive, ( String )ps.getDirective( directive ));
		}
	}

	@Override
	protected T onCreateModule() {
		return this.createModule( node );
	}

	@SuppressWarnings("unchecked")
	private T createModule( ComponentNode<T,U,V> node ){
		this.parseDirectives(node);
		T component = node.getFactory().createModule();
		this.notifyListeners( new ComponentFactoryEvent( this, node.getFactory(), FactoryEvents.COMPONENT_CREATED ));
		for( ComponentNode<?,?,?> child: node.getChildren())
			createModule( (ComponentNode<T,U,V>) child );
		return component;
		
	}
}
