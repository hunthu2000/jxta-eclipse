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
package net.osgi.jxta.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import net.jxta.document.Advertisement;
import net.osgi.jxta.component.IComponentChangedListener.ServiceChange;

public class JxtaComponentNode<T extends Object> implements IJxtaComponentNode<T>{

	private IJxtaComponent<T> component;
	private IJxtaComponentNode<?> parent;	
	private Collection<IJxtaComponent<?>> children;
	private ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
	
	public JxtaComponentNode( IJxtaComponentNode<?> parent, IJxtaComponent<T> component ) {
		this.component = component;
		this.parent = parent;
		this.children = new ArrayList<IJxtaComponent<?>>();
	}

	/**
	 * Get the create date
	 */
	@Override
	public Date getCreateDate(){
		return component.getCreateDate();
	}

	/**
	 * Return true if the component is a root
	 * @return
	 */
	@Override
	public boolean isRoot(){
		return ( this.parent == null );
	}
	/**
	 * Get the parent of the component
	 * @return
	 */
	@Override
	public IJxtaComponentNode<?> getParent(){
		return parent;
	}

	@Override
	public T getModule() {
		return this.component.getModule();
	}

	@Override
	public Object getProperty(Object key) {
		return component.getProperty(key);
	}

	
	@Override
	public void putProperty(Object key, Object value) {
		component.putProperty(key, value);	
	}

	/**
	 * Iterates through all the property keys
	 */
	@Override
	public Iterator<?> iterator() {
		return this.component.iterator();
	}

	@Override
	public void addChild( IJxtaComponent<?> child ){
		this.children.add( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, ServiceChange.CHILD_ADDED ));
	}

	@Override
	public void removeChild( IJxtaComponent<?> child ){
		this.children.remove( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, ServiceChange.CHILD_REMOVED ));
	}

	@Override
	public Collection<IJxtaComponent<?>> getChildren(){
		return this.children;
	}

	@Override
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	@Override
	public Advertisement[] getAdvertisements() {
		return this.component.getAdvertisements();
	}
	
	/**
	 * Returns true if the component has advertisements
	 * @return
	 */
	@Override
	public boolean hasAdvertisements(){
		return this.component.hasAdvertisements();
	}

}
