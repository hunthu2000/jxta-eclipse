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
package net.osgi.jxse.log;

import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class LoggerFactory extends
		AbstractComponentFactory<LoggerComponent>{

	public LoggerFactory( LoggerPropertySource source ) {
		super( source, false );
		super.setCanCreate( true);
	}
	
	@Override
	protected LoggerComponent onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		LoggerComponent service = new LoggerComponent( this );
		super.setCompleted( true );
		return service;
	}
}