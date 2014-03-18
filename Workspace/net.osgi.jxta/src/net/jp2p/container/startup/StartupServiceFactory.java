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
package net.jp2p.container.startup;

import net.jp2p.container.ContainerFactory;
import net.jp2p.container.Jp2pContainerPropertySource;
import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.context.Jp2pContext.Components;
import net.jp2p.container.factory.AbstractPropertySourceFactory;
import net.jp2p.container.factory.IPropertySourceFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IJp2pDirectives.Directives;

public class StartupServiceFactory extends AbstractPropertySourceFactory
{
	public StartupServiceFactory( IContainerBuilder builder,IJp2pPropertySource<IJp2pProperties> parent) {
		super( builder, parent );
		super.setCanCreate( builder != null );
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
		IContainerBuilder builder = super.getBuilder();
		IPropertySourceFactory factory = builder.getFactory( Jp2pContext.Components.JP2P_CONTAINER.toString() );
		ContainerFactory cf = (ContainerFactory) factory;
		if( !cf.isAutoStart() )
			return;
		IJp2pWritePropertySource<IJp2pProperties> props = (IJp2pWritePropertySource<IJp2pProperties>) factory.getPropertySource();
		props.setDirective( Directives.AUTO_START, Boolean.TRUE.toString());
		Jp2pStartupPropertySource.setParentDirective(Directives.AUTO_START, super.getPropertySource());
		factory = builder.getFactory( Jp2pContext.Components.PERSISTENCE_SERVICE.toString() );
		if( factory == null ){
			builder.addFactoryToContainer( Components.PERSISTENCE_SERVICE.toString(), super.getParentSource(), true, false);
		}
			
	}
}
