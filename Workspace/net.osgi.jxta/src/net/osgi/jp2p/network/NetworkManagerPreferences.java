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
package net.osgi.jp2p.network;

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jp2p.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jp2p.properties.AbstractPreferences;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.ManagedProperty;

public class NetworkManagerPreferences extends AbstractPreferences<IJp2pProperties> implements INetworkManagerPreferences
{
	public NetworkManagerPreferences( IJp2pWritePropertySource<IJp2pProperties> source )
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
		IJp2pPropertySource<IJp2pProperties> source = super.getSource();
		return ( ConfigMode ) source.getProperty( NetworkManagerProperties.MODE );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setConfigMode(net.jxta.platform.NetworkManager.ConfigMode)
	 */
	@Override
	public void setConfigMode( ConfigMode mode ){
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
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
		IJp2pPropertySource<IJp2pProperties> source = super.getSource();
		return (URI)source.getProperty( NetworkManagerProperties.INSTANCE_HOME );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setHomeFolder(java.net.URI)
	 */
	@Override
	public void setHomeFolder( URI homeFolder ){
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
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
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
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
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
		String name = source.getIdentifier();
		PeerID pgId = IDFactory.newPeerID( PeerGroupID.defaultNetPeerGroupID, name.getBytes() );
		ManagedProperty<IJp2pProperties, Object> property = source.getOrCreateManagedProperty( NetworkManagerProperties.PEER_ID, pgId.toString(), false );
		String str = (String) property.getValue();
		URI uri = new URI( str );
		return (PeerID) IDFactory.fromURI( uri );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setPeerID(net.jxta.peer.PeerID)
	 */
	@Override
	public void setPeerID( PeerID peerID ){
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
		source.setProperty( NetworkManagerProperties.PEER_ID, peerID.toString() );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#getInstanceName()
	 */
	@Override
	public String getInstanceName(){
		IJp2pPropertySource<IJp2pProperties> source = super.getSource();
		return (String) source.getProperty( NetworkManagerProperties.INSTANCE_NAME );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setInstanceName(java.lang.String)
	 */
	@Override
	public void setInstanceName( String name ){
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
		source.setProperty( NetworkManagerProperties.INSTANCE_NAME, name );
	}
	
	/**
	 * Get the correct property from the given string
	 * @param property
	 * @param value
	 * @return
	 * @throws URISyntaxException
	 */
	public Object convertValue( IJp2pProperties id, String value ){
		if( !( id instanceof NetworkManagerProperties ))
			return null;
		NetworkManagerProperties props = (NetworkManagerProperties) id;
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
		if( value == null )
			return null;
		switch( props ){
		case CONFIG_PERSISTENT:
			return source.setProperty(id, Boolean.parseBoolean( value ));
		case INSTANCE_NAME:
		case INFRASTRUCTURE_ID:
			return value;
		case PEER_ID:
			URI uri;
			try {
				uri = new URI( value );
				return (PeerID) IDFactory.fromURI( uri );
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			break;
		case INSTANCE_HOME:
			this.setHomeFolder(value);
			return true;
		case MODE:
			this.setConfigMode( ConfigMode.valueOf(value));
			return true;
		default:
			return false;
		}
		return null;
	}
}
