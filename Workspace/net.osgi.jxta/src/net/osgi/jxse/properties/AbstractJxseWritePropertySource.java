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

public abstract class AbstractJxseWritePropertySource< T extends Object> 
extends AbstractJxsePropertySource<T> implements IJxseWritePropertySource<T, IJxseDirectives> {

	public AbstractJxseWritePropertySource( String bundleId, String identifier, String componentName) {
		this( bundleId, identifier, componentName, 0);
	}

	protected AbstractJxseWritePropertySource( String bundleId, String identifier, String componentName, int depth ) {
		super( bundleId, identifier, componentName );
	}

	protected AbstractJxseWritePropertySource( String componentName, IJxsePropertySource<?,IJxseDirectives> parent ) {
		super( componentName, parent );
	}

	@Override
	public ManagedProperty<T,Object> getOrCreateManagedProperty(T id, Object value, boolean derived ) {
		ManagedProperty<T,Object> select = super.getManagedProperty(id);
		if( select == null ){
			select = new ManagedProperty<T, Object>( id, value, derived );
			super.setManagedProperty( select );
		}
		return select;
	}
	
	@Override
	public boolean setProperty(T id, Object value) {
		return this.setProperty(id, value, null, false );
	}

	protected boolean setProperty(T id, Object value, boolean derived ) {
		return this.setProperty(id, value, null, derived );
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected boolean setProperty(T id, Object value, IJxseValidator<T,Object> validator, boolean derived ) {
		IJxseWritePropertySource source = this;
		ManagedProperty select = source.getOrCreateManagedProperty(id, value, derived);
		if( validator != null )
			select.setValidator(validator);
		return select.setValue(value);
	}
		
	/**
	 * Set the directive
	 * @param id
	 * @param value
	 * @return
	 */
	public boolean setDirective(IJxseDirectives id, Object value) {
		return super.setDirective(id, value);
	}
}