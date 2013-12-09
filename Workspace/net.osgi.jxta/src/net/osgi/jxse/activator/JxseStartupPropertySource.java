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
package net.osgi.jxse.activator;

import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxseProperties;

public class JxseStartupPropertySource extends AbstractJxseWritePropertySource<IJxseProperties>{
	
	public JxseStartupPropertySource( JxseContextPropertySource parent ) {
		super( Components.STARTUP_SERVICE.toString(), parent );
	}

	@Override
	public IJxseProperties getIdFromString(String key) {
		return ContextProperties.valueOf( key );
	}

	/**
	 * Get the plugin ID
	 * @return
	 */
	public String getBundleId(){
		return (String) this.getProperty( ContextProperties.BUNDLE_ID );
	}

	/**
	 * Get the identifier
	 * @return
	 */
	public String getIdentifier(){
		return (String) this.getDirective( Directives.NAME );
	}

	@Override
	public Object getDefault( IJxseProperties id) {
		if(!( id instanceof ContextProperties ))
			return null;
		ContextProperties cp = (ContextProperties )id;
		switch( cp ){
		default:
			break;
		}
		return null;
	}

	@Override
	public boolean validate( IJxseProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}
