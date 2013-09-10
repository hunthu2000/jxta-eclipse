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
package net.osgi.jxse.context;

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.preferences.properties.IJxseDirectives;
import net.osgi.jxse.preferences.properties.IJxsePropertySource;

public class JxseContextPreferences implements IJxseContextPreferences
{
	private  IJxsePropertySource<ContextProperties, IJxseDirectives> source;
	
	public JxseContextPreferences( IJxsePropertySource<ContextProperties, IJxseDirectives> source )
	{
		this.source = source;
	}


	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getRendezVousAutostart()
	 */
	@Override
	public boolean getRendezVousAutostart( ){
		Object retval = this.source.getProperty( ContextProperties.RENDEZVOUZ_AUTOSTART );
		if( retval == null )
			return false;
		return Boolean.parseBoolean(( String )retval);
	}

	public void setRendezVousAutostart( boolean autostart ){
		this.source.setProperty( ContextProperties.RENDEZVOUZ_AUTOSTART, autostart );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getConfigMode()
	 */
	@Override
	public ConfigMode getConfigMode( ){
		return ConfigMode.valueOf( (String) this.source.getProperty( ContextProperties.CONFIG_MODE ));
	}

	public void setConfigMode( ConfigMode mode ){
		this.source.setProperty( ContextProperties.CONFIG_MODE, mode );
	}

	public void setConfigMode( String mode ){
		this.setConfigMode( ConfigMode.valueOf(mode ));
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getHomeFolder()
	 */
	@Override
	public URI getHomeFolder( ) throws URISyntaxException{
		return (URI) this.source.getProperty( ContextProperties.HOME_FOLDER );
	}

	public void setHomeFolder( URI homeFolder ){
		this.source.setProperty( ContextProperties.HOME_FOLDER, homeFolder.getPath() );
	}

	public void setHomeFolder( String homeFolder ){
		String folder = homeFolder;
		String[] split = homeFolder.split("[$]");
		if( split.length > 1 ){
			  folder = System.getProperty( split[0] ) + split[1]; 
		}
		this.source.setProperty( ContextProperties.HOME_FOLDER, folder );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getPeerID()
	 */
	@Override
	public PeerID getPeerID() throws URISyntaxException{
		String name = this.source.getIdentifier();
		PeerID pgId = IDFactory.newPeerID( PeerGroupID.defaultNetPeerGroupID, name.getBytes() );
		String str = (String) this.source.getProperty( ContextProperties.PEER_ID);
		URI uri = new URI( str );
		return (PeerID) IDFactory.fromURI( uri );
	}

	public void setPeerID( PeerID peerID ){
		this.source.setProperty( ContextProperties.PEER_ID, peerID.toString() );
	}
}
