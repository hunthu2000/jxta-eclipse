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

import net.osgi.jxse.properties.IJxseDirectives.Directives;

public abstract class AbstractJxseWritePropertySource 
extends AbstractJxsePropertySource implements IJxseWritePropertySource<IJxseProperties> {

	public AbstractJxseWritePropertySource( String bundleId, String identifier, String componentName) {
		this( bundleId, identifier, componentName, 0);
	}

	protected AbstractJxseWritePropertySource( String bundleId, String identifier, String componentName, int depth ) {
		super( bundleId, identifier, componentName );
	}

	protected AbstractJxseWritePropertySource( String componentName, IJxsePropertySource<IJxseProperties> parent ) {
		super( componentName, parent );
		setDirectiveFromParent( Directives.AUTO_START, this );
	}

	@Override
	public ManagedProperty<IJxseProperties,Object> getOrCreateManagedProperty(IJxseProperties id, Object value, boolean derived ) {
		ManagedProperty<IJxseProperties,Object> select = super.getManagedProperty(id);
		if( select == null ){
			select = new ManagedProperty<IJxseProperties, Object>( id, value, derived );
			super.setManagedProperty( select );
		}
		return select;
	}
	
	@Override
	public boolean setProperty(IJxseProperties id, Object value) {
		return this.setProperty(id, value, null, false );
	}

	protected boolean setProperty(IJxseProperties id, Object value, boolean derived ) {
		return this.setProperty(id, value, null, derived );
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected boolean setProperty(IJxseProperties id, Object value, IJxseValidator<IJxseProperties,Object> validator, boolean derived ) {
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
	@Override
	public boolean setDirective(IJxseDirectives id, String value) {
		return super.setDirective(id, value);
	}
}