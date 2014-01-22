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

import java.net.URI;
import java.net.URISyntaxException;

import net.jp2p.container.Jp2pContainerPropertySource;
import net.jp2p.container.IJxseServiceContainer.ContextProperties;
import net.jp2p.container.persistence.AbstractPreferences;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.utils.ProjectFolderUtils;
import net.jp2p.container.utils.Utils;
import net.jxta.id.IDFactory;
import net.jxta.platform.NetworkManager.ConfigMode;

public class Jp2pContainerPreferences extends AbstractPreferences<String, Object>
{
	public Jp2pContainerPreferences( Jp2pContainerPropertySource source )
	{
		super( source );
	}


	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getRendezVousAutostart()
	 */
	public boolean getRendezVousAutostart( ){
		IJp2pPropertySource<IJp2pProperties> source = super.getSource();
		Object retval = source.getProperty( ContextProperties.RENDEZVOUZ_AUTOSTART );
		if( retval == null )
			return false;
		return Boolean.parseBoolean(( String )retval);
	}

	public void setRendezVousAutostart( boolean autostart ){
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) super.getSource();
		source.setProperty( ContextProperties.RENDEZVOUZ_AUTOSTART, autostart );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getConfigMode()
	 */
	public ConfigMode getConfigMode( ){
		IJp2pPropertySource<IJp2pProperties> source = super.getSource();
		return ConfigMode.valueOf( (String)source.getProperty( ContextProperties.CONFIG_MODE ));
	}

	public void setConfigMode( ConfigMode mode ){
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) super.getSource();
		source.setProperty( ContextProperties.CONFIG_MODE, mode );
	}

	public void setConfigMode( String mode ){
		this.setConfigMode( ConfigMode.valueOf(mode ));
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getHomeFolder()
	 */
	public URI getHomeFolder( ) throws URISyntaxException{
		IJp2pPropertySource<IJp2pProperties> source = super.getSource();
		return (URI) source.getProperty( ContextProperties.HOME_FOLDER );
	}

	public void setHomeFolder( URI homeFolder ){
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) super.getSource();
		source.setProperty( ContextProperties.HOME_FOLDER, homeFolder );
	}
	
	@Override
	public String convertFrom(IJp2pProperties id) {
		Object value = super.getSource().getProperty(id);
		if( value != null )
			return value.toString();
		return null;
	}

	@Override
	public Object convertTo( IJp2pProperties props, String value ){
		if(( props == null ) || ( Utils.isNull(value )))
			return false;
		if( !( props instanceof ContextProperties )){
			return value;
		}
		ContextProperties id = (ContextProperties) props;
		switch( id ){
		case CONFIG_MODE:
			return ConfigMode.valueOf( value );
		case HOME_FOLDER:
			Jp2pContainerPropertySource source = (Jp2pContainerPropertySource) super.getSource();
			String bundleId = source.getBundleId();
			return ProjectFolderUtils.getParsedUserDir(value, bundleId );
		case PEER_ID:
			URI uri = URI.create(value);
			try {
				return IDFactory.fromURI( uri );
			} catch (URISyntaxException e) {
				throw new RuntimeException( e );
			}
		case PORT:
			return Integer.parseInt(value);
		default:
			return value;
		}
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
