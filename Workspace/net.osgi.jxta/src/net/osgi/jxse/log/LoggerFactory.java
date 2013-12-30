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

import java.util.logging.Logger;

import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class LoggerFactory extends
		AbstractComponentFactory<LoggerPropertySource>{

	private Logger logger = Logger.getLogger(LoggerFactory.class.getName());

	
	public LoggerFactory( BuilderContainer container, IJxsePropertySource<IJxseProperties> parent ) {
		super( container, parent, true );
	}

	@Override
	public String getComponentName() {
		return Components.LOGGER_SERVICE.toString();
	}

	@Override
	public LoggerPropertySource onCreatePropertySource() {
		LoggerPropertySource source = new LoggerPropertySource( super.getParentSource());
		return source;
	}

	@Override
	protected LoggerComponent onCreateComponent( IJxsePropertySource<IJxseProperties> properties) {
		LoggerComponent service = new LoggerComponent( this );
		super.setCompleted( true );
		return service;
	}
	
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		String msg = event.getBuilderEvent().toString() + ": " + event.getFactory().getComponentName();
		logger.log( JxseLevel.JXSELEVEL, msg );
		System.out.println(msg);
	}
}