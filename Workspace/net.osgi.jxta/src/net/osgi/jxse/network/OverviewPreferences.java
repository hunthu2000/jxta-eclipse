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
import java.net.URISyntaxException;
import java.util.Iterator;

import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jxse.properties.AbstractPreferences;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.properties.ManagedProperty;

public class OverviewPreferences extends AbstractPreferences<IJxseProperties> implements INetworkPreferences{

	public static final String S_OVERVIEW = "Overview";

	public OverviewPreferences( IJxseWritePropertySource<IJxseProperties> source ) {
		super( source );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkPreferences#setPropertyFromString(net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties, java.lang.String)
	 */
	@Override
	public Object convertValue( IJxseProperties key, String value ){
		if(!( key instanceof NetworkConfiguratorProperties ))
			return null;
		NetworkConfiguratorProperties id = (NetworkConfiguratorProperties) key;
		switch( id ){
		case MODE:
			return ConfigMode.valueOf( value );
		case STORE_HOME:
			return  URI.create(value);
		case PEER_ID:
			URI uri = URI.create(value);
			try {
				return IDFactory.fromURI( uri );
			} catch (URISyntaxException e) {
				throw new RuntimeException( e );
			}
		case DESCRIPTION:
		case HOME:
		case NAME:
			return value;
		default:
			break;
		}
		return null;
	}	

	/**
	 * Create a default value if this is requested as attribute and adds it to the source if it is not present 
	 * @param id
	 * @return
	 */
	@Override
	public Object createDefaultValue( IJxseProperties key ){
		if( !ManagedProperty.isCreated( super.getSource().getManagedProperty(key)))
			return null;
		
		if(!( key instanceof NetworkConfiguratorProperties ))
			return null;
		NetworkConfiguratorProperties id = (NetworkConfiguratorProperties) key;
		Object value = null;
		switch( id ){
		case PEER_ID:
			String name = super.getSource().getIdentifier();
			value = IDFactory.newPeerID( PeerGroupID.defaultNetPeerGroupID, name.getBytes() );
			break;
		default:
			break;
		}
		if( value != null )
			super.getSource().getOrCreateManagedProperty(id, value, false );
		return null;
	}

	/**
	 * Fill the configurator with the given properties from a key string
	 * @param configurator
	 * @param property
	 * @param value
	*/
	@Override
	public boolean fillConfigurator( NetworkConfigurator configurator ){
		Iterator<IJxseProperties> iterator = super.getSource().propertyIterator();
		boolean retval = true;
		while( iterator.hasNext() ){
			IJxseProperties id = (IJxseProperties) iterator.next();
			retval &= fillConfigurator(configurator, id, super.getSource().getManagedProperty(id));
		}
		return retval;
	}

	/**
	 * Fill the configurator with the given properties
	 * @param configurator
	 * @param property
	 * @param value
	 */
	public static boolean fillConfigurator( NetworkConfigurator configurator, IJxseProperties key, ManagedProperty<IJxseProperties,Object> property ){
		boolean retval = true;
		Object value = property.getValue();
		if( value == null )
			return false;
		if(!( key instanceof NetworkConfiguratorProperties ))
			return false;
		NetworkConfiguratorProperties id = (NetworkConfiguratorProperties) key;		
		switch( id ){
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
