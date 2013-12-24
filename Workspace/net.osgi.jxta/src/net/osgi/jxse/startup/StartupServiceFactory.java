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
package net.osgi.jxse.startup;

import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.builder.container.BuilderContainer;
import net.osgi.jxse.context.IJxseServiceContext;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class StartupServiceFactory extends AbstractComponentFactory<JxseStartupService> implements ICompositeBuilderListener<ComponentNode<?>>
{
	private BuilderContainer container;
	
	public StartupServiceFactory( BuilderContainer container, JxseStartupPropertySource source) {
		super(source );
		this.container = container;
		super.setCanCreate( this.container != null );
	}

	@Override
	protected JxseStartupService onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		JxseStartupService service = new JxseStartupService( this.container, (JxseStartupPropertySource) super.getPropertySource() );
		if( JxseStartupPropertySource.isAutoStart( super.getPropertySource()))
			service.initialise();
		return service;
	}

	@Override
	public void notifyChange( ComponentBuilderEvent<ComponentNode<?>> event) {
		if(!( event.getModule() instanceof IJxseServiceContext ))
			return;
	}
}
