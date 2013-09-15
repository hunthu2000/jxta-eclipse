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
package net.osgi.jxse.network;

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
import net.osgi.jxse.context.IJxseServiceContext.ContextDirectives;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class NetworkManagerFactory extends AbstractComponentFactory<NetworkManager, NetworkManagerProperties, ContextDirectives> {
		
	public NetworkManagerFactory( IJxsePropertySource<NetworkManagerProperties, ContextDirectives> propertySource ) {
		super( propertySource );
	}

	@Override
	protected void onParseDirectivePriorToCreation( ContextDirectives directive, Object value) {
		switch( directive ){
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
	protected void onParseDirectiveAfterCreation( NetworkManager component,	ContextDirectives directive, Object value) {
	}

	@Override
	protected NetworkManager onCreateModule( IJxsePropertySource<NetworkManagerProperties, ContextDirectives> properties) {
		// Removing any existing configuration?
		NetworkManagerPreferences<ContextDirectives> preferences = 
				new NetworkManagerPreferences<ContextDirectives>( properties );
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
			// Creation of the network manager
			NetworkManager manager = null;
			manager = new NetworkManager( preferences.getConfigMode(), name, file.toURI());
			return manager;
		} catch (Exception e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
			return null;
		}
	}
}