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
import java.net.URISyntaxException;
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
import net.osgi.jxse.preferences.properties.IJxsePropertySource;

public class NetworkManagerFactory extends AbstractComponentFactory<NetworkManager, NetworkManagerProperties, ContextDirectives> {
		
	private NetworkManagerPreferences<ContextDirectives> preferences;
	
	public NetworkManagerFactory( IJxsePropertySource<NetworkManagerProperties, ContextDirectives> propertySource ) {
		super( propertySource );
		preferences = new NetworkManagerPreferences<ContextDirectives>( propertySource );
		this.fillDefaultValues();
	}

	protected void fillDefaultValues() {
		this.addProperty( NetworkManagerProperties.INSTANCE_NAME, super.getPropertySource().getIdentifier() );
		try {
			this.addProperty( NetworkManagerProperties.INSTANCE_HOME, preferences.getHomeFolder() );
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		this.addProperty( NetworkManagerProperties.MODE, preferences.getConfigMode() );	
	}

	public void addProperty( NetworkManagerProperties key, Object value ){
		if(!( key instanceof NetworkManagerProperties) || ( value == null ))
			return;
		if(!( value instanceof String )){
			super.getPropertySource().setProperty(key, value);
			return;
		}
		String str = ( String )value;
		switch(( NetworkManagerProperties )key ){
		case INSTANCE_HOME:
			this.preferences.setHomeFolder( str );
			break;
		case INSTANCE_NAME:
			this.preferences.setInstanceName( str );
			break;
		case MODE:
			this.preferences.setConfigMode( str );
			break;
		default:
			super.addProperty(key, value);
			break;

		}
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
	protected NetworkManager onCreateModule() {
		// Removing any existing configuration?
		String name = (String)super.getPropertySource().getProperty( NetworkManagerProperties.INSTANCE_NAME );
		super.addProperty( NetworkManagerProperties.INSTANCE_NAME, name );
		Path path = Paths.get(( URI )super.getPropertySource().getProperty( NetworkManagerProperties.INSTANCE_HOME ));
		if(!Files.exists(path, LinkOption.NOFOLLOW_LINKS ))
			try {
				Files.createDirectories( path );
			} catch (IOException e1) {
				e1.printStackTrace();
				return null;
			}
		File file = path.toFile();
		// Creation of the network manager
		NetworkManager manager = null;
		try {
			manager = new NetworkManager(( 
					NetworkManager.ConfigMode )super.getPropertySource().getProperty( NetworkManagerProperties.MODE ), 
					name, file.toURI());
			return manager;
		} catch (Exception e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
			return null;
		}
	}
}