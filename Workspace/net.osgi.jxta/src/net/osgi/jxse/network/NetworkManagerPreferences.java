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

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jxse.properties.AbstractPreferences;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class NetworkManagerPreferences<T extends IJxseDirectives> extends AbstractPreferences<NetworkManagerProperties, T> implements INetworkManagerPreferences<T>
{
	public NetworkManagerPreferences( IJxseWritePropertySource<NetworkManagerProperties, T> source )
	{
		super( source );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getConfigMode()
	 */
	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#getConfigMode()
	 */
	@Override
	public ConfigMode getConfigMode( ){
		IJxsePropertySource<NetworkManagerProperties, T> source = super.getSource();
		return ( ConfigMode ) source.getProperty( NetworkManagerProperties.MODE );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setConfigMode(net.jxta.platform.NetworkManager.ConfigMode)
	 */
	@Override
	public void setConfigMode( ConfigMode mode ){
		IJxseWritePropertySource<NetworkManagerProperties, T> source = super.getSource();
		source.setProperty( NetworkManagerProperties.MODE, mode );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setConfigMode(java.lang.String)
	 */
	@Override
	public void setConfigMode( String mode ){
		this.setConfigMode( ConfigMode.valueOf(mode ));
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getHomeFolder()
	 */
	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#getHomeFolder()
	 */
	@Override
	public URI getHomeFolder( ) throws URISyntaxException{
		IJxsePropertySource<NetworkManagerProperties, T> source = super.getSource();
		return (URI)source.getProperty( NetworkManagerProperties.INSTANCE_HOME );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setHomeFolder(java.net.URI)
	 */
	@Override
	public void setHomeFolder( URI homeFolder ){
		IJxseWritePropertySource<NetworkManagerProperties, T> source = super.getSource();
		source.setProperty( NetworkManagerProperties.INSTANCE_HOME, homeFolder );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setHomeFolder(java.lang.String)
	 */
	@Override
	public void setHomeFolder( String homeFolder ){
		String folder = homeFolder;
		String[] split = homeFolder.split("[$]");
		if( split.length > 1 ){
			  folder = System.getProperty( split[0] ) + split[1]; 
		}
		IJxseWritePropertySource<NetworkManagerProperties, T> source = super.getSource();
		source.setProperty( NetworkManagerProperties.INSTANCE_HOME, folder );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getPeerID()
	 */
	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#getPeerID()
	 */
	@Override
	public PeerID getPeerID() throws URISyntaxException{
		IJxsePropertySource<NetworkManagerProperties, T> source = super.getSource();
		String name = source.getIdentifier();
		PeerID pgId = IDFactory.newPeerID( PeerGroupID.defaultNetPeerGroupID, name.getBytes() );
		String str = (String) source.getProperty( NetworkManagerProperties.PEER_ID);
		URI uri = new URI( str );
		return (PeerID) IDFactory.fromURI( uri );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setPeerID(net.jxta.peer.PeerID)
	 */
	@Override
	public void setPeerID( PeerID peerID ){
		IJxseWritePropertySource<NetworkManagerProperties, T> source = super.getSource();
		source.setProperty( NetworkManagerProperties.PEER_ID, peerID.toString() );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#getInstanceName()
	 */
	@Override
	public String getInstanceName(){
		IJxsePropertySource<NetworkManagerProperties, T> source = super.getSource();
		return (String) source.getProperty( NetworkManagerProperties.INSTANCE_NAME );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setInstanceName(java.lang.String)
	 */
	@Override
	public void setInstanceName( String name ){
		IJxseWritePropertySource<NetworkManagerProperties, T> source = super.getSource();
		source.setProperty( NetworkManagerProperties.INSTANCE_NAME, name );
	}
	
	/**
	 * Get the correct property from the given string
	 * @param property
	 * @param value
	 * @return
	 * @throws URISyntaxException
	 */
	public boolean setPropertyFromString( NetworkManagerProperties id, String value ){
		IJxseWritePropertySource<NetworkManagerProperties, T> source = super.getSource();
		switch( id ){
		case CONFIG_PERSISTENT:
			return source.setProperty(id, Boolean.parseBoolean( value ));
		case INSTANCE_NAME:
		case INFRASTRUCTURE_ID:
		case PEER_ID:
			return source.setProperty(id, value);
		case INSTANCE_HOME:
			this.setHomeFolder(value);
			return true;
		case MODE:
			this.setConfigMode( ConfigMode.valueOf(value));
			return true;
		default:
			return false;
		}
	}
}
