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
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.properties.AbstractPreferences;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.utils.ProjectFolderUtils;
import net.osgi.jxse.utils.Utils;

public class JxseContextPreferences extends AbstractPreferences<IJxseProperties>
{
	public JxseContextPreferences( JxseContextPropertySource source )
	{
		super( source );
	}


	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getRendezVousAutostart()
	 */
	public boolean getRendezVousAutostart( ){
		IJxsePropertySource<IJxseProperties> source = super.getSource();
		Object retval = source.getProperty( ContextProperties.RENDEZVOUZ_AUTOSTART );
		if( retval == null )
			return false;
		return Boolean.parseBoolean(( String )retval);
	}

	public void setRendezVousAutostart( boolean autostart ){
		IJxseWritePropertySource<IJxseProperties> source = super.getSource();
		source.setProperty( ContextProperties.RENDEZVOUZ_AUTOSTART, autostart );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getConfigMode()
	 */
	public ConfigMode getConfigMode( ){
		IJxsePropertySource<IJxseProperties> source = super.getSource();
		return ConfigMode.valueOf( (String)source.getProperty( ContextProperties.CONFIG_MODE ));
	}

	public void setConfigMode( ConfigMode mode ){
		IJxseWritePropertySource<IJxseProperties> source = super.getSource();
		source.setProperty( ContextProperties.CONFIG_MODE, mode );
	}

	public void setConfigMode( String mode ){
		this.setConfigMode( ConfigMode.valueOf(mode ));
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getHomeFolder()
	 */
	public URI getHomeFolder( ) throws URISyntaxException{
		IJxsePropertySource<IJxseProperties> source = super.getSource();
		return (URI) source.getProperty( ContextProperties.HOME_FOLDER );
	}

	public void setHomeFolder( URI homeFolder ){
		IJxseWritePropertySource<IJxseProperties> source = super.getSource();
		source.setProperty( ContextProperties.HOME_FOLDER, homeFolder );
	}

	@Override
	public Object convertValue( IJxseProperties props, String value ){
		if(( props == null ) || ( Utils.isNull(value )))
			return false;
		if( !( props instanceof ContextProperties ))
			return false;
		ContextProperties id = (ContextProperties) props;
		switch( id ){
		case CONFIG_MODE:
			return ConfigMode.valueOf( value );
		case HOME_FOLDER:
			JxseContextPropertySource source = (JxseContextPropertySource) super.getSource();
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
