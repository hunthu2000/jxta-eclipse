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
package net.osgi.jp2p.container;

import java.io.File;
import java.net.URI;

import net.osgi.jp2p.container.IJxseServiceContainer.ContextProperties;
import net.osgi.jp2p.factory.IComponentFactory.Components;
import net.osgi.jp2p.properties.AbstractJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pProperties.Jp2pProperties;
import net.osgi.jp2p.utils.ProjectFolderUtils;
import net.osgi.jp2p.validator.ClassValidator;
import net.osgi.jp2p.validator.RangeValidator;

public class Jp2pContainerPropertySource extends AbstractJp2pWritePropertySource{

	public static final String DEF_HOME_FOLDER = "${user.home}/.jxse/${bundle-id}";
	public static final int DEF_MIN_PORT = 1000;
	public static final int DEF_MAX_PORT = 9999;
	public static final int DEF_PORT = 9715;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Jp2pContainerPropertySource( String bundleId) {
		super( bundleId, Components.JP2P_CONTAINER.toString() );
		this.setProperty( ContextProperties.BUNDLE_ID, bundleId, 
				new ClassValidator( Jp2pProperties.BUNDLE_ID, String.class ), false );
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
	public String getIdentifier() {
		return super.getIdentifier();
	}

	@Override
	public IJp2pProperties getIdFromString(String key) {
		return ContextProperties.valueOf( key );
	}

	@Override
	public Object getDefault( IJp2pProperties id) {
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
		case RENDEZVOUZ_AUTOSTART:
			return true;
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
	public boolean validate( IJp2pProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}
