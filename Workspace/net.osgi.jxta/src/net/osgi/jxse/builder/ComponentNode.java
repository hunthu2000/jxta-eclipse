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
import net.osgi.jxse.preferences.properties.IJxseDirectives;

public class ComponentNode<T extends Object, U extends Enum<U>, V extends IJxseDirectives> implements Comparable<ComponentNode<?,?,?>> {

	private IComponentFactory<T,U,V> factory;
	
	private ComponentNode<?,?,?> parent;
	
	private Collection<ComponentNode<IComponentFactory<?,?,?>,?,?>> children;
	
	public ComponentNode( IComponentFactory<T,U,V> factory ) {
		this.factory = factory;
		children = new TreeSet<ComponentNode<IComponentFactory<?,?,?>,?,?>>();
	}

	ComponentNode( IComponentFactory<T,U,V> factory, ComponentNode<?,?,?> parent ) {
		this( factory );
		this.parent = parent;
	}

	public ComponentNode<?,?,?> getParent() {
		return parent;
	}

	
	public IComponentFactory<T,U,V> getFactory() {
		return factory;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ComponentNode<IComponentFactory<?,?,?>,?,?> addChild( IComponentFactory<?,?,?> factory ){
		ComponentNode<IComponentFactory<?,?,?>,?,?> node = new ComponentNode( factory, this );
		children.add( node );
		return node;
	}

	public boolean removeChild( IComponentFactory<?,?,?> factory ){
		for( ComponentNode<IComponentFactory<?,?,?>,?,?> nd: children ){
			if( nd.getFactory().equals( factory )){
				return children.remove(nd);
			}
		}
		return false;
	}

	public int nrOfChildren(){
		return this.children.size();
	}
	
	public ComponentNode<?,?,?>[] getChildren(){
		return this.children.toArray( new ComponentNode<?,?,?>[ this.children.size() ] );
	}

	@Override
	public int compareTo(ComponentNode<?,?,?> arg0) {
		if( arg0 == null )
			return 1;
		if(( this.factory == null ) && ( arg0.getFactory() == null ))
				return 0;
		if(( this.factory != null ) && ( arg0.getFactory() == null ))
				return 1;
		if(( this.factory == null ) && ( arg0.getFactory() != null ))
				return -1;

		return this.factory.getComponentName().compareTo( arg0.getFactory().getComponentName());
	}
}
