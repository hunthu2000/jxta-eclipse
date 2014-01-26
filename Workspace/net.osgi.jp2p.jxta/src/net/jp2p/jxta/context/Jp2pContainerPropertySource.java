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
package net.jp2p.jxta.context;

import java.io.File;
import java.net.URI;

import net.jp2p.container.IJp2pContainer.ContainerProperties;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.properties.AbstractJp2pWritePropertySource;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.utils.ProjectFolderUtils;
import net.jp2p.container.validator.ClassValidator;

public class Jp2pContainerPropertySource extends AbstractJp2pWritePropertySource{

	public static final String DEF_HOME_FOLDER = "${user.home}/.jxse/${bundle-id}";
	public static final int DEF_MIN_PORT = 1000;
	public static final int DEF_MAX_PORT = 9999;
	public static final int DEF_PORT = 9715;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Jp2pContainerPropertySource( String bundleId) {
		super( bundleId, Jp2pContext.Components.JP2P_CONTAINER.toString() );
		this.setProperty( ContainerProperties.BUNDLE_ID, bundleId, 
				new ClassValidator( ContainerProperties.BUNDLE_ID, String.class ), false );
		this.setProperty( ContainerProperties.HOME_FOLDER, ProjectFolderUtils.getParsedUserDir(DEF_HOME_FOLDER, bundleId),
				new ClassValidator( ContainerProperties.HOME_FOLDER, URI.class ), false);
	}

	/**
	 * Get the bundle id
	 * @return
	 */
	public String getBundleId(){
		return (String) super.getProperty( ContainerProperties.BUNDLE_ID );
	}
	
	@Override
	public IJp2pProperties getIdFromString(String key) {
		return ContainerProperties.valueOf( key );
	}

	@Override
	public Object getDefault( IJp2pProperties id) {
		if(!( id instanceof ContainerProperties ))
			return null;
		ContainerProperties cp = (ContainerProperties )id;
		String str = null;
		switch( cp ){
		case HOME_FOLDER:
			String bundle_id = (String) super.getProperty( ContainerProperties.BUNDLE_ID );
			str = ProjectFolderUtils.getParsedUserDir( DEF_HOME_FOLDER, bundle_id ).getPath();
			File file = new File( str );
			return file.toURI();
		case BUNDLE_ID:
			return (String) super.getProperty( ContainerProperties.BUNDLE_ID );
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
