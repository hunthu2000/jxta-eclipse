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

import net.osgi.jxse.utils.Utils;

public abstract class AbstractJxsePropertySource< T extends Enum<T>, U extends IJxseDirectives> implements IJxsePropertySource<T, U> {

	private static final String S_CONTEXT = "context";
	private static final String S_ERR_COMPONENT_NAME_NULL = "The component name cannot be null";
	
	private Map<T,ManagedProperty<T,Object>> properties;
	private Map<U,Object> directives;
	private Collection<IJxsePropertySource<?,?>> children;

	private int depth = 0;
	private String context_id, id_root;
	private String bundleId, identifier, componentName;
	
	public AbstractJxsePropertySource( String bundleId, String identifier, String componentName) {
		this( bundleId, identifier, componentName, 0);
	}

	protected AbstractJxsePropertySource( String bundleId, String identifier, String componentName, int depth ) {
		properties = new HashMap<T,ManagedProperty<T,Object>>();
		directives = new HashMap<U,Object>();
		this.bundleId = bundleId;
		this.context_id = this.bundleId + "." + componentName.toLowerCase();
		this.id_root = this.bundleId;
		this.identifier = identifier;
		this.componentName = componentName;
		children = new ArrayList<IJxsePropertySource<?,?>>();
		this.depth = depth;
	}

	public AbstractJxsePropertySource( String id, String bundleId, String identifier, String componentName) {
		this( id, bundleId, identifier, componentName, 0);
	}

	protected AbstractJxsePropertySource( String id, String bundleId, String identifier, String componentName, int depth ) {
		properties = new HashMap<T,ManagedProperty<T,Object>>();
		directives = new HashMap<U,Object>();
		this.context_id = id;
		if( Utils.isNull( this.context_id ))
			this.context_id = this.bundleId + "." + componentName.toLowerCase();
		this.id_root = this.getIdRoot(this.context_id);
		this.bundleId = bundleId;
		this.identifier = identifier;
		this.componentName = componentName;
		children = new ArrayList<IJxsePropertySource<?,?>>();
		this.depth = depth;
	}

	public String getId() {
		return context_id;
	}

	public String getIdRoot() {
		return id_root;
	}

	public void setId(String id) {
		if( Utils.isNull( id ))
			this.context_id = this.bundleId + "." + componentName.toLowerCase();
		else
			this.context_id = id;
		this.id_root = this.getIdRoot( this.context_id );
	}

	@Override
	public String getBundleId() {
		return this.bundleId;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	protected void setIdentifier(String identifier) {
		this.identifier = identifier;
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
		ManagedProperty<T,Object> select = properties.get( id );
		if( select == null )
			return null;
		return select.getValue();
	}

	@Override
	public boolean setProperty(T id, Object value) {
		if( value == null )
			return false;
		ManagedProperty<T,Object> select = properties.get( id );
		if( select == null )
			properties.put( id, new ManagedProperty<T,Object>( id, value ));
		else
			select.setValue(value);
		return true;
	}
	
	@Override
	public Object getDefault(T id) {
		ManagedProperty<T,Object> select = properties.get( id );
		if( select == null )
			return null;
		return select.getDefaultValue();
	}

	public ManagedProperty<T,Object> getManagedProperty( T id ){
		return properties.get(id);
	}
	
	@Override
	public Iterator<T> propertyIterator() {
		return this.properties.keySet().iterator();
	}

	@Override
	public Object getDirective(U id) {
		return directives.get( id );
	}

	@Override
	public boolean setDirective(U id, Object value) {
		if( value == null )
			return false;
		directives.put( id, value );
		return true;
	}

	@Override
	public Iterator<U> directiveIterator() {
		return directives.keySet().iterator();
	}


	public void addChild( IJxsePropertySource<?, ?> child ){
		this.children.add( child );
	}

	public void removeChild( IJxsePropertySource<?, ?> child ){
		this.children.remove( child );
	}

	@Override
	public IJxsePropertySource<?, ?>[] getChildren() {
		return this.children.toArray(new IJxsePropertySource[children.size()]);
	}
	
	/**
	 * Get the root of the id
	 * @param id
	 * @return
	 */
	private String getIdRoot( String id ){
		String name = this.getComponentName().toLowerCase();
		if( Utils.isNull( name ))
			throw new NullPointerException( S_ERR_COMPONENT_NAME_NULL );
		if( id != null ){
			if( id.contains( S_CONTEXT))
				id = id.replace("context", name);
			else
				id = id.replace("context", "");
			id += "." + name;
		}else
			id = this.getBundleId() + "." + name;
		return id;
	}}