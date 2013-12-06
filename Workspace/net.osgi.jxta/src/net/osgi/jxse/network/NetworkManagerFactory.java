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

import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class NetworkManagerFactory extends AbstractComponentFactory<NetworkManager, NetworkManagerProperties, IJxseDirectives> 
implements IPeerGroupProvider{
		
	public NetworkManagerFactory( IJxsePropertySource<NetworkManagerProperties, IJxseDirectives> propertySource ) {
		super( propertySource );
	}

	@Override
	protected void onParseDirectivePriorToCreation( IJxseDirectives directive, Object value) {
		switch(( IJxseDirectives.Directives )directive ){
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
	protected NetworkManager onCreateModule( IJxsePropertySource<NetworkManagerProperties, IJxseDirectives> properties) {
		// Removing any existing configuration?
		NetworkManagerPreferences<IJxseDirectives> preferences = 
				new NetworkManagerPreferences<IJxseDirectives>( (IJxseWritePropertySource<NetworkManagerProperties, IJxseDirectives>) properties );
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
			return new NetworkManager( preferences.getConfigMode(), name, file.toURI());
		} catch (Exception e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean complete() {
		IJxsePropertySource<NetworkManagerProperties, IJxseDirectives> properties = super.getPropertySource();
		Object value = properties.getDirective( Directives.AUTO_START );
		if( value == null )
			value = Boolean.FALSE.toString();
		if( Boolean.parseBoolean( (String) value )){
			try {
				super.getModule().startNetwork();
				return super.complete();
			} catch (PeerGroupException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public PeerGroup getPeerGroup() {
		if(!super.isCompleted() )
			return null;
		return super.getModule().getNetPeerGroup();
	}

	@Override
	public String getPeerGroupName() {
		return IPeerGroupProvider.S_NET_PEER_GROUP;
	}
}