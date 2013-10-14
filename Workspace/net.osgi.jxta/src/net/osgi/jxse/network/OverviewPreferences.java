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
package net.osgi.jxse.network;

import java.io.File;
import java.net.URI;
import java.util.Iterator;

import net.jxta.peer.PeerID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class OverviewPreferences implements INetworkPreferences{

	public static final String S_OVERVIEW = "Overview";

	private IJxseWritePropertySource<NetworkConfiguratorProperties, IJxseDirectives> source;
	
	public OverviewPreferences( IJxseWritePropertySource<NetworkConfiguratorProperties, IJxseDirectives> source ) {
		this.source = source;
	}

	@Override
	public String getName(){
		return this.source.getComponentName();
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkPreferences#setPropertyFromString(net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties, java.lang.String)
	 */
	@Override
	public void setPropertyFromString( NetworkConfiguratorProperties id, String value ){
		URI uri = null;
		switch( id ){
		case MODE:
			source.setProperty( id, ConfigMode.valueOf( value ));
			break;
		case STORE_HOME:
			uri = URI.create(value);
			source.setProperty( id, uri);
			break;
		case PEER_ID:
			uri = URI.create(value);
			source.setProperty( id, PeerID.create(uri ));
			break;
		case DESCRIPTION:
		case HOME:
		case NAME:
			source.setProperty( id, value );
			break;
		default:
			break;
		}
	}	

	/**
	 * Fill the configurator with the given properties from a key string
	 * @param configurator
	 * @param property
	 * @param value
	*/
	@Override
	public boolean fillConfigurator( NetworkConfigurator configurator ){
		Iterator<NetworkConfiguratorProperties> iterator = source.propertyIterator();
		boolean retval = true;
		while( iterator.hasNext() ){
			NetworkConfiguratorProperties id = iterator.next();
			retval &= fillConfigurator(configurator, id, source.getProperty(id));
		}
		return retval;
	}

	/**
	 * Fill the configurator with the given properties
	 * @param configurator
	 * @param property
	 * @param value
	 */
	public static boolean fillConfigurator( NetworkConfigurator configurator, NetworkConfiguratorProperties property, Object value ){
		boolean retval = true;
		switch( property ){
		case MODE:
			configurator.setMode((( ConfigMode )value).ordinal() );
			break;
		case PEER_ID:
			configurator.setPeerID(( PeerID ) value );
			break;
		case DESCRIPTION:
			configurator.setDescription(( String )value );
			break;
		case HOME:
			configurator.setHome(( File )value );
			break;
		case STORE_HOME:
			configurator.setStoreHome(( URI ) value );
			break;
		case NAME:
			configurator.setName(( String ) value );
			break;
		default:
			retval = false;
			break;
		}	
		return retval;
	}

}
