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
package net.osgi.jp2p.jxta.pipe;

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeID;
import net.osgi.jp2p.jxta.pipe.PipePropertySource;
import net.osgi.jp2p.jxta.pipe.PipePropertySource.PipeServiceProperties;
import net.osgi.jp2p.properties.AbstractPreferences;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.ManagedProperty;

public class PipeAdvertisementPreferences extends AbstractPreferences{

	private PeerGroup peergroup;
	
	public PipeAdvertisementPreferences( IJp2pWritePropertySource<IJp2pProperties> source, PeerGroup peergroup ) {
		super( source );
		this.peergroup = peergroup;
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkPreferences#setPropertyFromString(net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties, java.lang.String)
	 */
	@Override
	public Object convertValue( IJp2pProperties id, String value ){
		if( !( id instanceof PipeServiceProperties ))
			return null;
		PipeServiceProperties pid = ( PipeServiceProperties )id;
		switch( pid ){
		case PIPE_ID:
			URI uri = URI.create(value);
			try {
				return IDFactory.fromURI( uri );
			} catch (URISyntaxException e) {
				throw new RuntimeException( e );
			}
		case TYPE:
			return PipePropertySource.PipeServiceTypes.valueOf( value );
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
		
		if( !( id instanceof PipeServiceProperties ))
			return null;
		PipeServiceProperties pid = ( PipeServiceProperties )id;
		switch( pid ){
		case PIPE_ID:
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
		PipeID pgId = (PipeID) createDefaultValue( PipeServiceProperties.PIPE_ID );
		ManagedProperty<IJp2pProperties, Object> property = source.getOrCreateManagedProperty( PipeServiceProperties.PIPE_ID, pgId.toString(), false );
		String str = (String) property.getValue();
		URI uri = new URI( str );
		return (PipeID) IDFactory.fromURI( uri );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setPeerID(net.jxta.peer.PeerID)
	 */
	public void setPipeID( PipeID pipeID ){
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
		source.setProperty( PipeServiceProperties.PIPE_ID, pipeID.toString() );
	}
	
}
