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
package net.jp2p.endpoint.servlethttp.factory;

import net.jp2p.container.ContainerFactory;
import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.factory.AbstractFilterFactory;
import net.jp2p.container.factory.filter.ComponentCreateFilter;
import net.jp2p.container.factory.filter.IComponentFactoryFilter;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jxta.impl.endpoint.servlethttp.ServletHttpTransport;

public class HttpServiceFactory extends AbstractFilterFactory<ServletHttpTransport>{
		
	public HttpServiceFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parentSource ) {
		super( container, parentSource );
	}

	@Override
	public String getComponentName() {
		return HttpPropertySource.S_HTTP_SERVICE;
	}
	
	@Override
	protected IJp2pPropertySource<IJp2pProperties> onCreatePropertySource() {
		HttpPropertySource source = new HttpPropertySource( super.getParentSource());
		return source;
	}

	@Override
	protected IComponentFactoryFilter createFilter() {
		return new ComponentCreateFilter<IJp2pComponent<ServletHttpTransport>, ContainerFactory>( BuilderEvents.COMPONENT_CREATED, Jp2pContext.Components.JP2P_CONTAINER.toString(), this );
	}

	@Override
	protected HttpModule onCreateComponent(IJp2pPropertySource<IJp2pProperties> source ) {
		HttpModuleFactory hmf = new HttpModuleFactory( (HttpPropertySource) source );
		hmf.createModules();
		return new HttpModule(( HttpPropertySource ) source );
	}	 
}