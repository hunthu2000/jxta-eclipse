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
import java.util.Date;
import java.util.Iterator;

import net.jxta.document.Advertisement;
import net.osgi.jxse.component.IComponentChangedListener.ServiceChange;

public class JxseComponentNode<T extends Object, U extends Enum<U>> implements IJxseComponentNode<T,U>{

	private IJxseComponent<T,U> component;
	private IJxseComponentNode<?,?> parent;	
	private Collection<IJxseComponent<?,?>> children;
	private ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
	
	public JxseComponentNode( IJxseComponentNode<?,?> parent, IJxseComponent<T,U> component ) {
		this.component = component;
		this.parent = parent;
		this.children = new ArrayList<IJxseComponent<?,?>>();
	}

	/**
	 * Get the id
	 */
	@Override
	public String getId(){
		return component.getId();
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
	public IJxseComponentNode<?,?> getParent(){
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

	@Override
	public void addChild( IJxseComponent<?,?> child ){
		this.children.add( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, ServiceChange.CHILD_ADDED ));
	}

	@Override
	public void removeChild( IJxseComponent<?,?> child ){
		this.children.remove( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, ServiceChange.CHILD_REMOVED ));
	}

	@Override
	public Collection<IJxseComponent<?,?>> getChildren(){
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

	@Override
	public Iterator<U> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

}
