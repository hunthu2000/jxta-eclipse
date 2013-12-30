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

import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.context.ContextFactory;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class StartupServiceFactory extends AbstractComponentFactory<BuilderContainer>
{
	public StartupServiceFactory( BuilderContainer container,IJxsePropertySource<IJxseProperties> parent) {
		super( container, parent );
		super.setCanCreate( container != null );
	}

	@Override
	public String getComponentName() {
		return Components.STARTUP_SERVICE.toString();
	}

	@Override
	protected JxseStartupPropertySource onCreatePropertySource() {
		JxseStartupPropertySource source = new JxseStartupPropertySource( (JxseContextPropertySource) super.getParentSource());
		return source;
	}

	@Override
	public void extendContainer() {
		BuilderContainer container = super.getContainer();
		IComponentFactory<?> factory = container.getFactory( Components.JXSE_CONTEXT.toString() );
		ContextFactory cf = (ContextFactory) factory;
		if( !cf.isAutoStart() )
			return;
		factory = container.getFactory( Components.NET_PEERGROUP_SERVICE.toString() );
		if( factory == null )
			factory = container.addFactoryToContainer( Components.NET_PEERGROUP_SERVICE.name(), cf, true, false );
		IJxseWritePropertySource<IJxseProperties> props = (IJxseWritePropertySource<IJxseProperties>) factory.getPropertySource();
		props.setDirective( Directives.AUTO_START, Boolean.TRUE.toString());
		JxseStartupPropertySource.setParentDirective(Directives.AUTO_START, super.getPropertySource());
	}

	@Override
	protected JxseStartupService onCreateComponent( IJxsePropertySource<IJxseProperties> properties) {
		JxseStartupService service = new JxseStartupService( super.getContainer(), (JxseStartupPropertySource) super.getPropertySource() );
		if( JxseStartupPropertySource.isAutoStart( super.getPropertySource()))
			service.initialise();
		return service;
	}
}
