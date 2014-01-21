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
package net.osgi.jp2p.log;

import java.util.logging.Logger;

import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.context.Jp2pContext;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public class LoggerFactory extends
		AbstractComponentFactory<LoggerPropertySource>{

	private Logger logger = Logger.getLogger(LoggerFactory.class.getName());

	
	public LoggerFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent ) {
		super( container, parent );
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