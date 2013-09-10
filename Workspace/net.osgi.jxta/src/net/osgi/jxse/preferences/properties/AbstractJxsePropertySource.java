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
package net.osgi.jxse.preferences.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractJxsePropertySource< T extends Enum<T>, U extends IJxseDirectives> implements IJxsePropertySource<T, U> {

	private Map<T,Object> properties;
	private Map<U,Object> directives;
	private Collection<IJxsePropertySource<?,?>> children;

	private int depth = 0;
	private String context_id;
	private String bundleId, identifier, componentName;
	
	public AbstractJxsePropertySource( String bundleId, String identifier, String componentName) {
		this( bundleId, identifier, componentName, 0);
	}

	protected AbstractJxsePropertySource( String bundleId, String identifier, String componentName, int depth ) {
		properties = new HashMap<T,Object>();
		directives = new HashMap<U,Object>();
		this.bundleId = bundleId;
		this.identifier = identifier;
		this.componentName = componentName;
		children = new ArrayList<IJxsePropertySource<?,?>>();
		this.depth = depth;
	}

	public String getId() {
		return context_id;
	}

	public void setId(String id) {
		this.context_id = id;
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
		return properties.get( id );
	}

	@Override
	public boolean setProperty(T id, Object value) {
		if( value == null )
			return false;
		properties.put( id, value );
		return true;
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


	protected void addChild( IJxsePropertySource<?, ?> child ){
		this.children.add( child );
	}

	protected void removeChild( IJxsePropertySource<?, ?> child ){
		this.children.remove( child );
	}

	@Override
	public IJxsePropertySource<?, ?>[] getChildren() {
		return this.children.toArray(new IJxsePropertySource[children.size()]);
	}
}