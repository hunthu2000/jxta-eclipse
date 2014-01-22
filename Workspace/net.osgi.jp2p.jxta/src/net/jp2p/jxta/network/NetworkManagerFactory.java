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
package net.jp2p.jxta.network;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jp2p.container.ContainerFactory;
import net.jp2p.container.Jp2pContainerPropertySource;
import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.component.Jp2pComponentNode;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.factory.AbstractFilterFactory;
import net.jp2p.container.filter.ComponentFilter;
import net.jp2p.container.filter.IComponentFactoryFilter;
import net.jp2p.container.properties.IJp2pDirectives;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IJp2pDirectives.Directives;
import net.jp2p.jxta.factory.JxtaFactoryUtils;
import net.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.jp2p.jxta.network.NetworkManagerPreferences;
import net.jp2p.jxta.network.NetworkManagerPropertySource;
import net.jp2p.jxta.network.NetworkManagerPropertySource.NetworkManagerDirectives;
import net.jp2p.jxta.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.jxta.platform.NetworkManager;

public class NetworkManagerFactory extends AbstractFilterFactory<NetworkManager>{
		
	public static final String S_WRN_NO_CONFIGURATOR = "Could not add network configurator";
	
	public NetworkManagerFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parentSource ) {
		super( container, parentSource );
	}

	@Override
	public String getComponentName() {
		return JxtaComponents.NETWORK_MANAGER.toString();
	}
	
	@Override
	protected IComponentFactoryFilter createFilter() {
		return new ComponentFilter<IJp2pComponent<NetworkManager>, ContainerFactory>( BuilderEvents.COMPONENT_CREATED, Jp2pContext.Components.JP2P_CONTAINER.toString(), this );
	}

	@Override
	protected NetworkManagerPropertySource onCreatePropertySource() {
		NetworkManagerPropertySource source = new NetworkManagerPropertySource( (Jp2pContainerPropertySource) super.getParentSource());
		NetworkManagerPropertySource.setParentDirective(Directives.AUTO_START, source);
		return source;
	}

	@Override
	public void extendContainer() {
		IContainerBuilder builder = super.getBuilder();
		JxtaFactoryUtils.getOrCreateChildFactory( builder, new String[0], super.getPropertySource(), JxtaComponents.NETWORK_CONFIGURATOR.toString(), true );
		PeerGroupPropertySource npps = (PeerGroupPropertySource) JxtaFactoryUtils.getOrCreateChildFactory( builder, new String[0], super.getParentSource(), JxtaComponents.NET_PEERGROUP_SERVICE.toString(), true ).getPropertySource();
		npps.setDirective( Directives.AUTO_START, this.getPropertySource().getDirective( Directives.AUTO_START ));
		super.extendContainer();
	}

	@Override
	protected void onParseDirectivePriorToCreation( IJp2pDirectives directive, Object value) {
		if(( directive != null ) && !directive.equals( Directives.CLEAR  ) && !directive.equals( NetworkManagerDirectives.CLEAR_CONFIG))
			return;
		Path path = Paths.get(( URI )super.getPropertySource().getProperty( NetworkManagerProperties.INSTANCE_HOME ));
		if(Files.exists(path, LinkOption.NOFOLLOW_LINKS )){
			File file = path.toFile();
			NetworkManager.RecursiveDelete( file );
		}
	}

	@Override
	protected IJp2pComponent<NetworkManager> onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		// Removing any existing configuration?
		NetworkManagerPreferences preferences = new NetworkManagerPreferences( (IJp2pWritePropertySource<IJp2pProperties>) properties );
		String name = preferences.getInstanceName();
		try {
			Path path = Paths.get( preferences.getHomeFolder() );
			if(!Files.exists(path, LinkOption.NOFOLLOW_LINKS )){
				try {
					Files.createDirectories( path );
				} catch (IOException e1) {
					e1.printStackTrace();
					return null;
				}
			}
			File file = path.toFile();
			NetworkManager manager = new NetworkManager( preferences.getConfigMode(), name, file.toURI());
			return new Jp2pComponentNode<NetworkManager>( super.getPropertySource(), manager );
		} catch (Exception e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
			return null;
		}
	}
}