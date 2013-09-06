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
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.preferences.IJxsePreferences;
import net.osgi.jxse.preferences.ProjectFolderUtils;
import net.osgi.jxse.utils.StringStyler;

public class NetworkManagerFactory extends AbstractComponentFactory<NetworkManager> {
		
	public static final String S_NETWORK_MANAGER_SERVICE = "NetworkManagerService";

	public enum NetworkManagerProperties{
		CONFIG_PERSISTENT,
		INFRASTRUCTURE_ID,
		INSTANCE_HOME,
		INSTANCE_NAME,
		MODE,
		PEER_ID;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	private IJxsePreferences preferences;
	
	public NetworkManagerFactory() {
		super( Components.NETWORK_MANAGER, false );
	}

	public NetworkManagerFactory( IJxsePreferences preferences ) {
		super( Components.NETWORK_MANAGER, false );
		this.preferences = preferences;
		this.fillDefaultValues();
	}

	public String getInstanceName(){
		return (String) super.getProperty( NetworkManagerProperties.INSTANCE_NAME );
	}
	
	public void setInstanceName( String instanceName ){
		super.addProperty( NetworkManagerProperties.INSTANCE_NAME, instanceName );
	}

	public String getInstanceHomeFolder(){
		URI uri = ( URI )super.getProperty( NetworkManagerProperties.INSTANCE_HOME );
		return uri.getPath();
	}
	
	public void setInstanceHomeFolder( String instanceHomeFolder ){
		URI uri = ProjectFolderUtils.getParsedUserDir( instanceHomeFolder, this.preferences.getPluginId() );
		super.addProperty( NetworkManagerProperties.INSTANCE_HOME, uri );
	}

	public String getConfigMode(){
		ConfigMode mode = (ConfigMode) super.getProperty( NetworkManagerProperties.MODE );
		return mode.name();
	}
	
	public void setConfigMode( String mode ){
		ConfigMode cm = ConfigMode.valueOf( mode );
		super.addProperty( NetworkManagerProperties.MODE, cm );
	}

	public IJxsePreferences getPreferences() {
		return preferences;
	}

	@Override
	protected void fillDefaultValues() {
		this.addProperty( NetworkManagerProperties.INSTANCE_NAME, preferences.getIdentifier() );
		try {
			this.addProperty( NetworkManagerProperties.INSTANCE_HOME, preferences.getHomeFolder() );
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		this.addProperty( NetworkManagerProperties.MODE, preferences.getConfigMode() );	
	}

	@Override
	public void addProperty( Object key, Object value ){
		if(!( key instanceof NetworkManagerProperties) || ( value == null ))
			return;
		if(!( value instanceof String )){
			super.addProperty(key, value);
			return;
		}
		String str = ( String )value;
		switch(( NetworkManagerProperties )key ){
		case INSTANCE_HOME:
			this.setInstanceHomeFolder( str );
			break;
		case INSTANCE_NAME:
			this.setInstanceName( str );
			break;
		case MODE:
			this.setConfigMode( str );
			break;
		default:
			super.addProperty(key, value);
			break;

		}
	}

	@Override
	protected void onParseDirectivePriorToCreation(Directives directive,
			String value) {
		switch( directive ){
		case CLEAR_CONFIG:
			Path path = Paths.get(( URI )super.getProperty( NetworkManagerProperties.INSTANCE_HOME ));
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
	protected void onParseDirectiveAfterCreation( NetworkManager component,	Directives directive, String value) {
	}

	@Override
	protected NetworkManager onCreateModule() {
		// Removing any existing configuration?
		String name = (String)super.getProperty( NetworkManagerProperties.INSTANCE_NAME );
		super.addProperty( NetworkManagerProperties.INSTANCE_NAME, name );
		Path path = Paths.get(( URI )super.getProperty( NetworkManagerProperties.INSTANCE_HOME ));
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
					NetworkManager.ConfigMode )super.getProperty( NetworkManagerProperties.MODE ), 
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