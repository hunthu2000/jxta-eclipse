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
package net.osgi.jxse.component;

import java.util.ArrayList;
import java.util.Collection;

import net.osgi.jxse.context.AbstractServiceContext;

public class JxseComponentNode<T extends Object> extends JxseComponent<T> implements IJxseComponentNode<T>{

	private ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
	private Collection<IJxseComponent<?>> children;

	public JxseComponentNode( T component ) {
		super( component);
		this.children = new ArrayList<IJxseComponent<?>>();
	}

	public JxseComponentNode( IJxseComponentNode<?> parent, T component ) {
		super( parent, component);
		this.children = new ArrayList<IJxseComponent<?>>();
	}

	@Override
	public void addChild( IJxseComponent<?> child ){
		this.children.add( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, AbstractServiceContext.ServiceChange.CHILD_ADDED ));
	}

	@Override
	public void removeChild( IJxseComponent<?> child ){
		this.children.remove( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, AbstractServiceContext.ServiceChange.CHILD_REMOVED ));
	}

	@Override
	public Collection<IJxseComponent<?>> getChildren(){
		return this.children;
	}

	@Override
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	/**
	 * add a module to the container. returns the JxseComponent, or null if something went wrong
	 * @param module
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static IJxseComponent<?> addModule( IJxseComponentNode node, Object module ){
		IJxseComponent<Object> component = null;
		if( module instanceof IJxseComponent )
			component = (IJxseComponent<Object>) module;
		else
			component = new JxseComponent( node, module );

		node.addChild( component );
		return component;
	}

	/**
	 * Remove a child from the context
	 * @param node
	 * @param module
	 */
	public static void removeModule( IJxseComponentNode<?> node, Object module ){
		Collection<IJxseComponent<?>> temp = new ArrayList<IJxseComponent<?>>( node.getChildren() );
		for( IJxseComponent<?> component: temp ){
			if( component.getModule().equals( module ))
				node.getChildren().remove(component);
		}
	}
}
