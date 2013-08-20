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

import java.util.Collection;
import java.util.TreeSet;

public class FactoryNode<T extends Object> implements Comparable<FactoryNode<?>> {

	private IComponentFactory<T> factory;
	
	private FactoryNode<?> parent;
	
	private Collection<FactoryNode<IComponentFactory<?>>> children;
	
	public FactoryNode( IComponentFactory<T> factory ) {
		this.factory = factory;
		children = new TreeSet<FactoryNode<IComponentFactory<? extends Object>>>();
	}

	FactoryNode( IComponentFactory<T> factory, FactoryNode<?> parent ) {
		this( factory );
		this.parent = parent;
	}

	public FactoryNode<?> getParent() {
		return parent;
	}

	
	public IComponentFactory<T> getFactory() {
		return factory;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public FactoryNode<IComponentFactory<?>> addChild( IComponentFactory<?> factory ){
		FactoryNode<IComponentFactory<?>> node = new FactoryNode( factory, this );
		children.add( node );
		return node;
	}

	public boolean removeChild( IComponentFactory<?> factory ){
		for( FactoryNode<IComponentFactory<?>> nd: children ){
			if( nd.getFactory().equals( factory )){
				return children.remove(nd);
			}
		}
		return false;
	}

	public int nrOfChildren(){
		return this.children.size();
	}
	
	public FactoryNode<?>[] getChildren(){
		return this.children.toArray( new FactoryNode<?>[ this.children.size() ] );
	}

	@Override
	public int compareTo(FactoryNode<?> arg0) {
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
