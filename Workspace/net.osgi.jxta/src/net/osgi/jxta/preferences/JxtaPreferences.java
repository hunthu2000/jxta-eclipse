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
package net.osgi.jxta.preferences;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxta.preferences.IJxtaContextProperties.ContextProperties;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.service.prefs.Preferences;

public class JxtaPreferences implements IJxtaPreferences
{
	private Preferences preferences;
	private Preferences jxtaPreferences;
	private String name;
	private String plugin_id;
	
	
	public JxtaPreferences( String plugin_id, String name )
	{
		preferences = ConfigurationScope.INSTANCE.getNode( plugin_id );
		jxtaPreferences = preferences.node(JXTA_SETTINGS);
		jxtaPreferences.put( ContextProperties.NAME.toString(), name );
		this.name = name;
		this.plugin_id = plugin_id;
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getName()
	 */
	@Override
	public String getIdentifier( ){
		return this.jxtaPreferences.get( ContextProperties.NAME.toString() , name );
	}

	public void setName( String nm ){
		this.jxtaPreferences.put( ContextProperties.NAME.toString() , nm );
	}

	@Override
	public String getPluginId() {
		return plugin_id;
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getRendezVousAutostart()
	 */
	@Override
	public boolean getRendezVousAutostart( ){
		return Boolean.valueOf( this.jxtaPreferences.get( ContextProperties.RENDEZVOUZ_AUTOSTART.toString(), String.valueOf( false )));
	}

	public void setRendezVousAutostart( boolean autostart ){
		this.jxtaPreferences.put( ContextProperties.RENDEZVOUZ_AUTOSTART.toString(), String.valueOf( autostart ));
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getConfigMode()
	 */
	@Override
	public ConfigMode getConfigMode( ){
		return ConfigMode.valueOf( this.jxtaPreferences.get( ContextProperties.CONFIG_MODE.toString(), NetworkManager.ConfigMode.EDGE.name() ));
	}

	public void setConfigMode( ConfigMode mode ){
		this.jxtaPreferences.put( ContextProperties.CONFIG_MODE.toString(), mode.name() );
	}

	public void setConfigMode( String mode ){
		this.setConfigMode( ConfigMode.valueOf(mode ));
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getHomeFolder()
	 */
	@Override
	public URI getHomeFolder( ) throws URISyntaxException{
		String str = this.jxtaPreferences.get( ContextProperties.HOME_FOLDER.toString(), ProjectFolderUtils.getDefaultUserDir( S_JXTA ).getPath());
		File file = new File( str );
		return file.toURI();
	}

	public void setHomeFolder( URI homeFolder ){
		this.jxtaPreferences.put( ContextProperties.HOME_FOLDER.toString(), homeFolder.getPath() );
	}

	public void setHomeFolder( String homeFolder ){
		String folder = homeFolder;
		String[] split = homeFolder.split("[$]");
		if( split.length > 1 ){
			  folder = System.getProperty( split[0] ) + split[1]; 
		}
		this.jxtaPreferences.put( ContextProperties.HOME_FOLDER.toString(), folder );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getPeerID()
	 */
	@Override
	public PeerID getPeerID() throws URISyntaxException{
		PeerID pgId = IDFactory.newPeerID( PeerGroupID.defaultNetPeerGroupID, name.getBytes() );
		String str = this.jxtaPreferences.get( ContextProperties.PEER_ID.toString(), pgId.toString());
		URI uri = new URI( str );
		return (PeerID) IDFactory.fromURI( uri );
	}

	public void setPeerID( PeerID peerID ){
		this.jxtaPreferences.put( ContextProperties.PEER_ID.toString(), peerID.toString() );
	}
}
