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

import java.io.File;
import java.net.URISyntaxException;

import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.context.IJxseServiceContext.ContextDirectives;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.preferences.properties.AbstractJxsePropertySource;
import net.osgi.jxse.utils.ProjectFolderUtils;
import net.osgi.jxse.utils.Utils;

public class JxseContextPropertySource extends AbstractJxsePropertySource<ContextProperties, ContextDirectives>{

	public static final String DEF_HOME_FOLDER = "${user.home}/.jxta/${plugin-id}";
	public static final int DEF_MIN_PORT = 1000;
	public static final int DEF_MAX_PORT = 9999;
	public static final int DEF_PORT = 9715;
	
	public JxseContextPropertySource( String bundleId, String identifier) {
		super( bundleId, identifier, Components.JXSE_CONTEXT.toString() );
		this.setProperty( ContextProperties.PLUGIN_ID, bundleId );
		this.setProperty( ContextProperties.IDENTIFIER, identifier );
	}

	/**
	 * Get the plugin ID
	 * @return
	 */
	public String getBundleId(){
		return (String) this.getProperty( ContextProperties.PLUGIN_ID );
	}

	/**
	 * Get the identifier
	 * @return
	 */
	public String getIdentifier(){
		return (String) this.getProperty( ContextProperties.IDENTIFIER );
	}

	public void setIdentifier( String identifier ){
		this.setProperty( ContextProperties.IDENTIFIER, identifier );	
		super.setIdentifier(identifier);
	}
	
	@Override
	public Object getDefault(ContextProperties id) {
		String str = null;
		switch( id ){
		case HOME_FOLDER:
			String plugin_id = (String) super.getProperty( ContextProperties.PLUGIN_ID );
			str = ProjectFolderUtils.getDefaultJxseDir( AbstractJxsePropertySource.S_JXTA, plugin_id ).getPath();
			File file = new File( str );
			return file.toURI();
		case CONFIG_MODE:
			return ConfigMode.valueOf( NetworkManager.ConfigMode.EDGE.name() );
		case RENDEZVOUZ_AUTOSTART:
			return true;
		case PEER_ID:
			str = ( String )this.getProperty( ContextProperties.IDENTIFIER);
			if( Utils.isNull(str))
				return null;
			PeerID pgId = IDFactory.newPeerID( PeerGroupID.defaultNetPeerGroupID, ( str.getBytes() ));
			try {
				return (PeerID) IDFactory.fromURI( pgId.toURI() );
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			break;
		case IDENTIFIER:
			return (String) super.getProperty( ContextProperties.IDENTIFIER );
		case PLUGIN_ID:
			return (String) super.getProperty( ContextProperties.PLUGIN_ID );
		case PORT:
			return DEF_PORT;
		default:
			break;
		}
		return null;
	}

	@Override
	public Object getDefaultDirectives(ContextDirectives id) {
		switch( id ){
		case AUTO_START:
			return true;
		case PEER_ID_CREATE:
			return true;
		case PEER_ID_PERSIST:
			return true;
		case CLEAR_CONFIG:
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public boolean validate(ContextProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}
