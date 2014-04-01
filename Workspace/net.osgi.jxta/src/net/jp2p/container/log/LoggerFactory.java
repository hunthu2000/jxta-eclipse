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
package net.jp2p.container.log;

import java.util.logging.Logger;

import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.factory.AbstractComponentFactory;
import net.jp2p.container.factory.ComponentBuilderEvent;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;

public class LoggerFactory extends
		AbstractComponentFactory<LoggerPropertySource>{

	private Logger logger = Logger.getLogger(LoggerFactory.class.getName());

	
	public LoggerFactory() {
		super.setCanCreate(true);
	}

	@Override
	public String getComponentName() {
		return Jp2pContext.Components.LOGGER_SERVICE.toString();
	}

	@Override
	public LoggerPropertySource onCreatePropertySource() {
		LoggerPropertySource source = new LoggerPropertySource( super.getParentSource());
		return source;
	}

	@Override
	protected LoggerComponent onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		LoggerComponent service = new LoggerComponent( this );
		super.setCompleted( true );
		return service;
	}
	
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		String contextName = Jp2pContext.getContextName( event.getFactory().getPropertySource() );
		String msg = event.getBuilderEvent().toString() + ": <" + contextName + ">:" + event.getFactory().getComponentName();
		logger.log( Jp2pLevel.JP2PLEVEL, msg );
		System.out.println(msg);
	}
}