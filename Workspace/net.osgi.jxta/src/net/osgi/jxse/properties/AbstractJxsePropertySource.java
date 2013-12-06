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
package net.osgi.jxse.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public abstract class AbstractJxsePropertySource< T extends Object> implements IJxsePropertySource<T, IJxseDirectives> {
	
	private Map<T,ManagedProperty<T,Object>> properties;
	private Map<IJxseDirectives,Object> directives;
	
	private IJxsePropertySource<?,?> parent;

	private Collection<IJxsePropertySource<?,?>> children;

	private int depth = 0;
	private String id_root;
	private String bundleId, componentName;
	
	public AbstractJxsePropertySource( String bundleId, String identifier, String componentName) {
		this( bundleId, identifier, componentName, 0);
	}

	protected AbstractJxsePropertySource( String bundleId, String identifier, String componentName, int depth ) {
		properties = new HashMap<T,ManagedProperty<T,Object>>();
		directives = new HashMap<IJxseDirectives,Object>();
		this.bundleId = bundleId;
		this.directives.put( IJxseDirectives.Directives.ID, this.bundleId + "." + componentName.toLowerCase() );
		this.id_root = this.bundleId;
		this.directives.put( IJxseDirectives.Directives.NAME, identifier );
		this.componentName = componentName;
		children = new ArrayList<IJxsePropertySource<?,?>>();
		this.depth = depth;
		this.parent = null;
	}

	protected AbstractJxsePropertySource( IJxsePropertySource<?,?> parent ) {
		this( parent.getBundleId(), parent.getIdentifier(), parent.getComponentName(), parent.getDepth() + 1 );
		this.parent = parent;
	}

	protected AbstractJxsePropertySource( String componentName, IJxsePropertySource<?,?> parent ) {
		this( parent );
		this.componentName = componentName;
	}

	public IJxsePropertySource<?,?> getParent(){
		return this.parent;
	}

	public String getId() {
		return (String) this.directives.get( IJxseDirectives.Directives.ID );
	}

	public String getIdRoot() {
		return id_root;
	}

	@Override
	public String getBundleId() {
		return this.bundleId;
	}

	@Override
	public String getIdentifier() {
		return (String) this.directives.get( IJxseDirectives.Directives.NAME );
	}

	@Override
	public String getComponentName() {
		return this.componentName;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public Object getProperty(T id) {
		ManagedProperty<T,Object> select = this.getManagedProperty(id);
		if( select == null )
			return null;
		return select.getValue();
	}

	protected boolean setManagedProperty( ManagedProperty<T,Object> property ) {
		this.properties.put( property.getKey(), property );
		return true;
	}
		
	@Override
	public Object getDefault(T id) {
		ManagedProperty<T,Object> select = this.getManagedProperty(id);
		if( select == null )
			return null;
		return select.getDefaultValue();
	}

	public ManagedProperty<T,Object> getManagedProperty( T id ){
		return properties.get( id );
	}
	
	@Override
	public Iterator<T> propertyIterator() {
		return this.properties.keySet().iterator();
	}

	@Override
	public Object getDirective( IJxseDirectives id) {
		return directives.get( id );
	}

	@Override
	public IJxseDirectives getDirectiveFromString( String id) {
		return IJxseDirectives.Directives.valueOf( id );
	}

	/**
	 * Set the directive
	 * @param id
	 * @param value
	 * @return
	 */
	public boolean setDirective( IJxseDirectives id, Object value) {
		if( value == null )
			return false;
		directives.put( id, value );
		return true;
	}

	@Override
	public Iterator<IJxseDirectives> directiveIterator() {
		return directives.keySet().iterator();
	}

	public boolean addChild( IJxsePropertySource<?, ?> child ){
		return this.children.add( child );
	}

	public void removeChild( IJxsePropertySource<?, ?> child ){
		this.children.remove( child );
	}

	public IJxsePropertySource<?,?> getChild( String componentName ){
		for( IJxsePropertySource<?,?> child: this.children ){
			if( child.getComponentName().equals(componentName ))
				return child;
		}
		return null;
	}

	@Override
	public IJxsePropertySource<?, ?>[] getChildren() {
		return this.children.toArray(new IJxsePropertySource[children.size()]);
	}
	
	public boolean isEmpty(){
		return this.properties.isEmpty();
	}

	@Override
	public String toString() {
		return super.toString() + "[" + this.getComponentName() + "]";
	}
}