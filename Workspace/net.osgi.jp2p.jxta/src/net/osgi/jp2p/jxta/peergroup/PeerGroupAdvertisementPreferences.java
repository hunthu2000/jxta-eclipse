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
package net.osgi.jp2p.jxta.peergroup;

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeID;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource.PeerGroupProperties;
import net.osgi.jp2p.properties.AbstractPreferences;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.ManagedProperty;

public class PeerGroupAdvertisementPreferences extends AbstractPreferences{

	private PeerGroup peergroup;
	
	public PeerGroupAdvertisementPreferences( IJp2pWritePropertySource<IJp2pProperties> source, PeerGroup peergroup ) {
		super( source );
		this.peergroup = peergroup;
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkPreferences#setPropertyFromString(net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties, java.lang.String)
	 */
	@Override
	public Object convertValue( IJp2pProperties id, String value ){
		if( !( id instanceof PeerGroupProperties ))
			return null;
		PeerGroupProperties pid = ( PeerGroupProperties )id;
		switch( pid ){
		case PEERGROUP_ID:
			URI uri = URI.create(value);
			try {
				return IDFactory.fromURI( uri );
			} catch (URISyntaxException e) {
				throw new RuntimeException( e );
			}
		default:
			break;
		}
		return null;
	}	

	/**
	 * Create a default value if this is requested as attribute and adds it to the source if it is not present 
	 * @param id
	 * @return
	 */
	@Override
	public Object createDefaultValue( IJp2pProperties id ){
		if( ManagedProperty.isCreated( super.getSource().getManagedProperty(id)))
			return null;
		
		if( !( id instanceof PeerGroupProperties ))
			return null;
		PeerGroupProperties pid = ( PeerGroupProperties )id;
		switch( pid ){
		case PEERGROUP_ID:
			return IDFactory.newPipeID( peergroup.getPeerGroupID() );
		default:
			break;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#getPeerID()
	 */
	public PipeID getPipeID() throws URISyntaxException{
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
		PipeID pgId = (PipeID) createDefaultValue( PeerGroupProperties.PEERGROUP_ID );
		ManagedProperty<IJp2pProperties, Object> property = source.getOrCreateManagedProperty( PeerGroupProperties.PEERGROUP_ID, pgId.toString(), false );
		String str = (String) property.getValue();
		URI uri = new URI( str );
		return (PipeID) IDFactory.fromURI( uri );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setPeerID(net.jxta.peer.PeerID)
	 */
	public void setPipeID( PipeID pipeID ){
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
		source.setProperty( PeerGroupProperties.PEERGROUP_ID, pipeID.toString() );
	}
	
}
