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
import java.util.Map;

import net.osgi.jxse.activator.IActivator;
import net.osgi.jxse.builder.ICompositeBuilderListener.FactoryEvents;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentFactoryEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Directives;

public class CompositeBuilder<T extends Object> extends AbstractComponentFactory<T> implements ICompositeBuilder<T> {

	private ComponentNode<T> node;
	
	private Collection<ICompositeBuilderListener> factoryListeners;
	
	public CompositeBuilder( ComponentNode<T> node) {
		super( node.getFactory().getComponentName() );
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
	protected void onParseDirectivePriorToCreation(Directives directive, String value) {
		this.onParseDirectivePriorToCreation(node, directive, value );
	}

	protected void onParseDirectivePriorToCreation( ComponentNode<T> node, Directives directive, String value) {
		IComponentFactory<?> parentFactory = null;
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
	protected void onParseDirectiveAfterCreation( T component, Directives directive, String value) {}

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectives( ComponentNode<T> node ){
		Map<Directives,String> directives = node.getFactory().getDirectives();
		if(( directives == null ) || ( directives.isEmpty()))
			return;
		for( Directives directive: directives.keySet())
			this.onParseDirectivePriorToCreation( node, directive, directives.get( directive ));
	}

	@Override
	protected T onCreateModule() {
		return this.createModule( node );
	}

	@SuppressWarnings("unchecked")
	private T createModule( ComponentNode<T> node ){
		this.parseDirectives(node);
		T component = node.getFactory().createModule();
		this.notifyListeners( new ComponentFactoryEvent( this, node.getFactory(), FactoryEvents.COMPONENT_CREATED ));
		for( ComponentNode<?> child: node.getChildren())
			createModule( (ComponentNode<T>) child );
		return component;
		
	}
	
	@Override
	protected void fillDefaultValues() {/* NOTHING NEEDED */}
}
