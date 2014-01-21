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

import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.container.ContainerFactory;
import net.osgi.jp2p.container.Jp2pContainerPropertySource;
import net.osgi.jp2p.context.Jp2pContext;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;

public class StartupServiceFactory extends AbstractComponentFactory<IContainerBuilder>
{
	public StartupServiceFactory( IContainerBuilder container,IJp2pPropertySource<IJp2pProperties> parent) {
		super( container, parent );
		super.setCanCreate( container != null );
	}

	@Override
	public String getComponentName() {
		return Jp2pContext.Components.STARTUP_SERVICE.toString();
	}

	@Override
	protected Jp2pStartupPropertySource onCreatePropertySource() {
		Jp2pStartupPropertySource source = new Jp2pStartupPropertySource( (Jp2pContainerPropertySource) super.getParentSource());
		return source;
	}

	@Override
	public void extendContainer() {
		IContainerBuilder container = super.getBuilder();
		IComponentFactory<?> factory = container.getFactory( Jp2pContext.Components.JP2P_CONTAINER.toString() );
		ContainerFactory cf = (ContainerFactory) factory;
		if( !cf.isAutoStart() )
			return;
		IJp2pWritePropertySource<IJp2pProperties> props = (IJp2pWritePropertySource<IJp2pProperties>) factory.getPropertySource();
		props.setDirective( Directives.AUTO_START, Boolean.TRUE.toString());
		Jp2pStartupPropertySource.setParentDirective(Directives.AUTO_START, super.getPropertySource());
	}

	@Override
	protected Jp2pStartupService onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		Jp2pStartupService service = new Jp2pStartupService( super.getBuilder(), (Jp2pStartupPropertySource) super.getPropertySource() );
		if( Jp2pStartupPropertySource.isAutoStart( super.getPropertySource()))
			service.initialise();
		return service;
	}
}
