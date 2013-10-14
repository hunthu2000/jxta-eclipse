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
import net.osgi.jxse.properties.AbstractPreferences;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.utils.ProjectFolderUtils;
import net.osgi.jxse.utils.Utils;

public class JxseContextPreferences extends AbstractPreferences<ContextProperties, IJxseDirectives.Directives> implements IJxseContextPreferences
{
	public JxseContextPreferences( IJxseWritePropertySource<ContextProperties, IJxseDirectives.Directives> source )
	{
		super( source );
	}


	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getRendezVousAutostart()
	 */
	@Override
	public boolean getRendezVousAutostart( ){
		IJxsePropertySource<ContextProperties,IJxseDirectives.Directives> source = super.getSource();
		Object retval = source.getProperty( ContextProperties.RENDEZVOUZ_AUTOSTART );
		if( retval == null )
			return false;
		return Boolean.parseBoolean(( String )retval);
	}

	public void setRendezVousAutostart( boolean autostart ){
		IJxseWritePropertySource<ContextProperties,IJxseDirectives.Directives> source = super.getSource();
		source.setProperty( ContextProperties.RENDEZVOUZ_AUTOSTART, autostart );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getConfigMode()
	 */
	@Override
	public ConfigMode getConfigMode( ){
		IJxsePropertySource<ContextProperties,IJxseDirectives.Directives> source = super.getSource();
		return ConfigMode.valueOf( (String)source.getProperty( ContextProperties.CONFIG_MODE ));
	}

	public void setConfigMode( ConfigMode mode ){
		IJxseWritePropertySource<ContextProperties,IJxseDirectives.Directives> source = super.getSource();
		source.setProperty( ContextProperties.CONFIG_MODE, mode );
	}

	public void setConfigMode( String mode ){
		this.setConfigMode( ConfigMode.valueOf(mode ));
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getHomeFolder()
	 */
	@Override
	public URI getHomeFolder( ) throws URISyntaxException{
		IJxsePropertySource<ContextProperties,IJxseDirectives.Directives> source = super.getSource();
		return (URI) source.getProperty( ContextProperties.HOME_FOLDER );
	}

	public void setHomeFolder( URI homeFolder ){
		IJxseWritePropertySource<ContextProperties,IJxseDirectives.Directives> source = super.getSource();
		source.setProperty( ContextProperties.HOME_FOLDER, homeFolder );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getPeerID()
	 */
	@Override
	public PeerID getPeerID() throws URISyntaxException{
		IJxsePropertySource<ContextProperties,IJxseDirectives.Directives> source = super.getSource();
		String name = source.getIdentifier();
		PeerID pgId = IDFactory.newPeerID( PeerGroupID.defaultNetPeerGroupID, name.getBytes() );
		String str = (String) source.getProperty( ContextProperties.PEER_ID);
		URI uri = new URI( str );
		return (PeerID) IDFactory.fromURI( uri );
	}

	public void setPeerID( PeerID peerID ){
		IJxseWritePropertySource<ContextProperties,IJxseDirectives.Directives> source = super.getSource();
		source.setProperty( ContextProperties.PEER_ID, peerID.toString() );
	}
	
	public boolean setPropertyFromString( ContextProperties id, String value ){
		if(( id == null ) || ( Utils.isNull(value )))
			return false;
		IJxseWritePropertySource<ContextProperties,IJxseDirectives.Directives> source = super.getSource();
		switch( id ){
		case CONFIG_MODE:
			this.setConfigMode( ConfigMode.valueOf( value ));
			break;
		case HOME_FOLDER:
			this.setHomeFolder( ProjectFolderUtils.getParsedUserDir(value, source.getBundleId()));
			break;
		case PEER_ID:
			URI uri = URI.create(value);
			try {
				this.setPeerID((PeerID) IDFactory.fromURI( uri ));
			} catch (URISyntaxException e) {
				throw new RuntimeException( e );
			}
			break;
		case PORT:
			source.setProperty(id, Integer.parseInt(value));
			break;
		default:
			source.setProperty(id, value);
			break;
		}
		return true;
	}
	
	/**
	 * Get the config modes
	 * @return
	 */
	public static final String[] getConfigModes(){
		ConfigMode[] modes = ConfigMode.values();
		String[] retval = new String[ modes.length ];
		for( int i=0; i<modes.length; i++ ){
			retval[i] = modes[i].name();
		}
		return retval;
	}


}
