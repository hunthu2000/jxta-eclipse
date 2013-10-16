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

import java.util.Iterator;

public class PropertySourceWrapper< T extends Object, U extends IJxseDirectives> implements IJxsePropertySource<T, U> {

	private IJxsePropertySource<T, U> source;
	
	public PropertySourceWrapper( IJxsePropertySource<T, U> source ) {
		this.source = source;
	}

	/**
	 * Get the property source
	 * @return
	 */
	protected  IJxsePropertySource<T, U> getSource(){
		return source;
	}
	
	public String getId() {
		return source.getId();
	}

	@Override
	public String getBundleId() {
		return source.getBundleId();
	}

	@Override
	public String getIdentifier() {
		return source.getIdentifier();
	}

	@Override
	public String getComponentName() {
		return source.getComponentName();
	}

	@Override
	public int getDepth() {
		return source.getDepth();
	}

	@Override
	public Object getDefault(T id) {
		return source.getDefault(id);
	}

	@Override
	public ManagedProperty<T, Object> getManagedProperty(T id) {
		return source.getManagedProperty(id);
	}

	@Override
	public Iterator<T> propertyIterator() {
		return this.source.propertyIterator();
	}

	@Override
	public boolean validate(T id, Object value) {
		return this.source.validate(id, value);
	}

	@Override
	public Object getDirective(U id) {
		return this.source.getDirective(id);
	}

	@Override
	public U getDirectiveFromString(String id) {
		return this.source.getDirectiveFromString(id);
	}

	@Override
	public Iterator<U> directiveIterator() {
		return this.directiveIterator();
	}
	
	@Override
	public IJxsePropertySource<?, ?>[] getChildren() {
		return this.source.getChildren();
	}

	@Override
	public T getIdFromString(String key) {
		return source.getIdFromString(key);
	}

	@Override
	public IJxsePropertySource<?, ?> getParent() {
		return source.getParent();
	}

	@Override
	public Object getProperty(T id) {
		return this.source.getProperty(id);
	}	
}