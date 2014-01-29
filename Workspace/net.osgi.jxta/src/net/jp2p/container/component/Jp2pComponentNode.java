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
package net.jp2p.container.component;

import java.util.ArrayList;
import java.util.Collection;

import net.jp2p.container.AbstractJp2pContainer;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;

public class Jp2pComponentNode<T extends Object> extends Jp2pComponent<T> implements IJp2pComponentNode<T>{

	private ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
	private Collection<IJp2pComponent<?>> children;

	public Jp2pComponentNode( IJp2pPropertySource<IJp2pProperties> source, T component ) {
		super( source, component);
		this.children = new ArrayList<IJp2pComponent<?>>();
	}

	@Override
	public boolean addChild( IJp2pComponent<?> child ){
		this.children.add( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, AbstractJp2pContainer.ServiceChange.CHILD_ADDED ));
		return true;
	}

	@Override
	public void removeChild( IJp2pComponent<?> child ){
		this.children.remove( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, AbstractJp2pContainer.ServiceChange.CHILD_REMOVED ));
	}

	@Override
	public IJp2pComponent<?>[] getChildren(){
		return this.children.toArray( new IJp2pComponent<?>[this.children.size()] );
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
	public static IJp2pComponent<?> addModule( IJp2pComponentNode node, Object module ){
		IJp2pComponent<Object> component = null;
		if( module instanceof IJp2pComponent )
			component = (IJp2pComponent<Object>) module;
		else
			component = new Jp2pComponent( node, module );

		node.addChild( component );
		return component;
	}

	/**
	 * Remove a child from the context
	 * @param node
	 * @param module
	 */
	public static void removeModule( IJp2pComponentNode<?> node, Object module ){
		for( IJp2pComponent<?> component: node.getChildren() ){
			if( component.getModule().equals( module ))
				node.removeChild(component);
		}
	}
}