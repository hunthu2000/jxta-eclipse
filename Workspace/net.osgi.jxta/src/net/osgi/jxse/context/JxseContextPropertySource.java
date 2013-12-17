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
import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.utils.ProjectFolderUtils;
import net.osgi.jxse.utils.Utils;
import net.osgi.jxse.validator.ClassValidator;
import net.osgi.jxse.validator.RangeValidator;

public class JxseContextPropertySource extends AbstractJxseWritePropertySource<IJxseProperties>{

	public static final String DEF_HOME_FOLDER = "${user.home}/.jxse/${bundle-id}";
	public static final int DEF_MIN_PORT = 1000;
	public static final int DEF_MAX_PORT = 9999;
	public static final int DEF_PORT = 9715;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JxseContextPropertySource( String bundleId, String identifier) {
		super( bundleId, identifier, Components.JXSE_CONTEXT.toString() );
		this.setProperty( ContextProperties.BUNDLE_ID, bundleId, 
				new ClassValidator( ContextProperties.CONFIG_MODE, String.class ), false );
		this.setProperty( ContextProperties.CONFIG_MODE, ConfigMode.EDGE, 
				new ClassValidator( ContextProperties.CONFIG_MODE, ConfigMode.class ), false);
		this.setProperty( ContextProperties.HOME_FOLDER, ProjectFolderUtils.getParsedUserDir(DEF_HOME_FOLDER, bundleId),
				new ClassValidator( ContextProperties.HOME_FOLDER, URI.class ), false);
		this.setProperty( ContextProperties.PORT, DEF_PORT,
				new RangeValidator( ContextProperties.PORT, 65535 ), false);
	}

	/**
	 * Get the bundle id
	 * @return
	 */
	public String getBundleId(){
		return (String) super.getProperty( ContextProperties.BUNDLE_ID );
	}
	
	@Override
	public IJxseProperties getIdFromString(String key) {
		return ContextProperties.valueOf( key );
	}

	@Override
	public Object getDefault( IJxseProperties id) {
		if(!( id instanceof ContextProperties ))
			return null;
		ContextProperties cp = (ContextProperties )id;
		String str = null;
		switch( cp ){
		case HOME_FOLDER:
			String bundle_id = (String) super.getProperty( ContextProperties.BUNDLE_ID );
			str = ProjectFolderUtils.getParsedUserDir( DEF_HOME_FOLDER, bundle_id ).getPath();
			File file = new File( str );
			return file.toURI();
		case CONFIG_MODE:
			return ConfigMode.EDGE;
		case RENDEZVOUZ_AUTOSTART:
			return true;
		case PEER_ID:
			str = ( String )this.getIdentifier();
			if( Utils.isNull(str))
				return null;
			PeerID pgId = IDFactory.newPeerID( PeerGroupID.defaultNetPeerGroupID, ( str.getBytes() ));
			try {
				return (PeerID) IDFactory.fromURI( pgId.toURI() );
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			break;
		case BUNDLE_ID:
			return (String) super.getProperty( ContextProperties.BUNDLE_ID );
		case PORT:
			return DEF_PORT;
		default:
			break;
		}
		return null;
	}

	@Override
	public boolean validate( IJxseProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}
