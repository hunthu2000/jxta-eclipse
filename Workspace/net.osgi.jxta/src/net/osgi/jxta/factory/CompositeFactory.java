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
package net.osgi.jxta.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.osgi.jxta.activator.IActivator;
import net.osgi.jxta.factory.ICompositeFactoryListener.FactoryEvents;

public class CompositeFactory<T extends Object> extends AbstractServiceComponentFactory<T> implements ICompositeFactory<T> {

	private FactoryNode<T> node;
	
	private Collection<ICompositeFactoryListener> factoryListeners;
	
	public CompositeFactory( FactoryNode<T> node) {
		super( node.getFactory().getComponentName() );
		this.node = node;
		this.factoryListeners = new ArrayList<ICompositeFactoryListener>();
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#addListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	@Override
	public void addListener( ICompositeFactoryListener listener ){
		this.factoryListeners.add( listener);
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#removeListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	@Override
	public void removeListener( ICompositeFactoryListener listener ){
		this.factoryListeners.remove( listener);
	}

	protected void notifyListeners( FactoryEvent event ){
		for( ICompositeFactoryListener listener: factoryListeners )
			listener.notifyFactoryCreated(event);
	}
 
	@Override
	protected void onParseDirectivePriorToCreation(Directives directive, String value) {
		this.onParseDirectivePriorToCreation(node, directive, value );
	}

	protected void onParseDirectivePriorToCreation( FactoryNode<T> node, Directives directive, String value) {
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
	private final void parseDirectives( FactoryNode<T> node ){
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
	private T createModule( FactoryNode<T> node ){
		this.parseDirectives(node);
		T component = node.getFactory().createModule();
		this.notifyListeners( new FactoryEvent( this, node.getFactory(), FactoryEvents.COMPONENT_CREATED ));
		for( FactoryNode<?> child: node.getChildren())
			createModule( (FactoryNode<T>) child );
		return component;
		
	}
	
	@Override
	protected void fillDefaultValues() {/* NOTHING NEEDED */}
}
