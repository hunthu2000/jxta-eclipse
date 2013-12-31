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
package net.osgi.jp2p.startup;

import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.context.ContainerFactory;
import net.osgi.jp2p.context.Jp2pContainerPropertySource;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;

public class StartupServiceFactory extends AbstractComponentFactory<ContainerBuilder>
{
	public StartupServiceFactory( ContainerBuilder container,IJp2pPropertySource<IJp2pProperties> parent) {
		super( container, parent );
		super.setCanCreate( container != null );
	}

	@Override
	public String getComponentName() {
		return Components.STARTUP_SERVICE.toString();
	}

	@Override
	protected JxseStartupPropertySource onCreatePropertySource() {
		JxseStartupPropertySource source = new JxseStartupPropertySource( (Jp2pContainerPropertySource) super.getParentSource());
		return source;
	}

	@Override
	public void extendContainer() {
		ContainerBuilder container = super.getBuilder();
		IComponentFactory<?> factory = container.getFactory( Components.JXSE_CONTEXT.toString() );
		ContainerFactory cf = (ContainerFactory) factory;
		if( !cf.isAutoStart() )
			return;
		factory = container.getFactory( Components.NET_PEERGROUP_SERVICE.toString() );
		if( factory == null )
			factory = container.addFactoryToContainer( Components.NET_PEERGROUP_SERVICE.name(), cf, true, false );
		IJp2pWritePropertySource<IJp2pProperties> props = (IJp2pWritePropertySource<IJp2pProperties>) factory.getPropertySource();
		props.setDirective( Directives.AUTO_START, Boolean.TRUE.toString());
		JxseStartupPropertySource.setParentDirective(Directives.AUTO_START, super.getPropertySource());
	}

	@Override
	protected JxseStartupService onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		JxseStartupService service = new JxseStartupService( super.getBuilder(), (JxseStartupPropertySource) super.getPropertySource() );
		if( JxseStartupPropertySource.isAutoStart( super.getPropertySource()))
			service.initialise();
		return service;
	}
}
