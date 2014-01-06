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
package net.osgi.jp2p.jxta.network;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.platform.NetworkManager;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.component.Jp2pComponentNode;
import net.osgi.jp2p.container.ContainerFactory;
import net.osgi.jp2p.container.Jp2pContainerPropertySource;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.jxta.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jp2p.jxta.network.NetworkManagerPreferences;
import net.osgi.jp2p.jxta.network.NetworkManagerPropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;

public class NetworkManagerFactory extends AbstractComponentFactory<NetworkManager>{
		
	public static final String S_WRN_NO_CONFIGURATOR = "Could not add network configurator";
	
	public NetworkManagerFactory( ContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parentSource ) {
		super( container, parentSource );
	}

	@Override
	public String getComponentName() {
		return JxtaComponents.NETWORK_MANAGER.toString();
	}
	
	@Override
	protected NetworkManagerPropertySource onCreatePropertySource() {
		NetworkManagerPropertySource source = new NetworkManagerPropertySource( (Jp2pContainerPropertySource) super.getParentSource());
		NetworkManagerPropertySource.setParentDirective(Directives.AUTO_START, source);
		return source;
	}

	@Override
	public void extendContainer() {
		ContainerBuilder container = super.getBuilder();
		IComponentFactory<?> ncf = container.getFactory( JxtaComponents.NETWORK_CONFIGURATOR.toString() );
		if( ncf == null )
			ncf = container.addFactoryToContainer( JxtaComponents.NETWORK_CONFIGURATOR.toString(), this, true, true );
		
		super.extendContainer();
	}

	@Override
	protected void onParseDirectivePriorToCreation( IJp2pDirectives directive, Object value) {
		switch(( IJp2pDirectives.Directives )directive ){
		case CLEAR_CONFIG:
			Path path = Paths.get(( URI )super.getPropertySource().getProperty( NetworkManagerProperties.INSTANCE_HOME ));
			if(Files.exists(path, LinkOption.NOFOLLOW_LINKS )){
				File file = path.toFile();
				NetworkManager.RecursiveDelete( file );
			}
			break;
		default:
			break;
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
	
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		switch( event.getBuilderEvent()){
		case COMPONENT_CREATED:
			if( !isComponentFactory( Components.JP2P_CONTAINER, event.getFactory() ))
				break;
			IComponentFactory<?> factory = event.getFactory();
			ContainerFactory cf = (ContainerFactory) factory;
			if( cf.isAutoStart() )
				this.createComponent();
			break;
		default:
			break;
		}
		super.notifyChange(event);
	}	
}