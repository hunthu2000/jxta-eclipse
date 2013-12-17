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

import java.util.Collection;
import java.util.TreeSet;

import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.utils.Utils;

public class ComponentNode<T extends Object, U extends Object> implements Comparable<ComponentNode<?,?>> {

	public static final String S_NODE = "Node:";
	
	private IComponentFactory<T,U> factory;
	
	private ComponentNode<?,?> parent;
	
	private Collection<ComponentNode<IComponentFactory<?,?>,?>> children;

	public ComponentNode() {
		this( null );
	}

	public ComponentNode( IComponentFactory<T,U> factory ) {
		this.factory = factory;
		children = new TreeSet<ComponentNode<IComponentFactory<?,?>,?>>();
	}

	ComponentNode( IComponentFactory<T,U> factory, ComponentNode<?,?> parent ) {
		this( factory );
		this.parent = parent;
	}

	public ComponentNode<?,?> getParent() {
		return parent;
	}

	
	public IComponentFactory<T,U> getFactory() {
		return factory;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ComponentNode<IComponentFactory<?,?>,?> addChild( IComponentFactory<?,?> factory ){
		ComponentNode<IComponentFactory<?,?>,?> node = new ComponentNode( factory, this );
		children.add( node );
		return node;
	}

	public boolean removeChild( IComponentFactory<?,?> factory ){
		for( ComponentNode<IComponentFactory<?,?>,?> nd: children ){
			if( nd.getFactory().equals( factory )){
				return children.remove(nd);
			}
		}
		return false;
	}

	public int nrOfChildren(){
		return this.children.size();
	}
	
	public ComponentNode<?,?>[] getChildren(){
		return this.children.toArray( new ComponentNode<?,?>[ this.children.size() ] );
	}

	@Override
	public int compareTo(ComponentNode<?,?> arg0) {
		if( arg0 == null )
			return 1;
		if(( this.factory == null ) && ( arg0.getFactory() == null ))
				return 0;
		if(( this.factory != null ) && ( arg0.getFactory() == null ))
				return 1;
		if(( this.factory == null ) && ( arg0.getFactory() != null ))
				return -1;

		return this.factory.getComponent().compareTo( arg0.getFactory().getComponent());
	}
	
	/**
	 * Get the child with the given name
	 * @param node
	 * @param componentName
	 * @return
	 */
	public static ComponentNode<?,?> getChild( ComponentNode<?,?> node, String componentName ){
		if( Utils.isNull( componentName ))
			return null;
		for( ComponentNode<?,?> child: node.getChildren()){
			if( child.getFactory().getPropertySource().getComponentName().equals( componentName ))
				return child;
		}
		return null;
	}

	@Override
	public String toString() {
		return S_NODE + this.getFactory().getPropertySource().getComponentName();
	}

}
