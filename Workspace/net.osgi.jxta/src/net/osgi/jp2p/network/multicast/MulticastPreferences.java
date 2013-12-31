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
package net.osgi.jp2p.network.multicast;

import java.util.Iterator;

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jp2p.network.INetworkPreferences;
import net.osgi.jp2p.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jp2p.partial.PartialPropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;

public class MulticastPreferences implements INetworkPreferences{

	public static final String S_MULTICAST_CONFIGURATION = "Multicast Configuration";
	
	private PartialPropertySource source;
	
	
	public MulticastPreferences( PartialPropertySource source ) {
		this.source = source;
	}

	/**
	 * Get the name of the preference store
	 * @return
	 */
	@Override
	public String getName()
	{
		return source.getComponentName();
	}
		
	@Override
	public boolean setPropertyFromString( IJp2pProperties property, String value) {
		if(!(property instanceof NetworkConfiguratorProperties ))
			return false;
		NetworkConfiguratorProperties id = (NetworkConfiguratorProperties) property;
		switch( id ){
		case MULTICAST_8ENABLED:
			source.setProperty( id, Boolean.parseBoolean( value ));
			break;
		case MULTICAST_8POOL_SIZE:
		case MULTICAST_8PORT:
		case MULTICAST_8SIZE:
			source.setProperty( id, Integer.parseInt( value ));
			break;
		case MULTICAST_8ADDRESS:
		case MULTICAST_8STATUS:
		case MULTICAST_8INTERFACE:
			source.setProperty( id, value );
			break;
		default:
			break;
		}
		return true;
	}	

	/**
	 * Fill the configurator with the given properties from a key string
	 * @param configurator
	 * @param property
	 * @param value
	*/
	@Override
	public boolean fillConfigurator( NetworkConfigurator configurator ){
		Iterator<IJp2pProperties> iterator = source.propertyIterator();
		boolean retval = true;
		while( iterator.hasNext() ){
			NetworkConfiguratorProperties id = (NetworkConfiguratorProperties) iterator.next();
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
		case MULTICAST_8ENABLED:
		case MULTICAST_8STATUS:
			configurator.setUseMulticast((boolean) value );
			break;
		case MULTICAST_8ADDRESS:
			configurator.setMulticastAddress(( String )value );
			break;
		case MULTICAST_8PORT:
			configurator.setMulticastPort(( Integer ) value );
			break;
		case MULTICAST_8POOL_SIZE:
			configurator.setMulticastPoolSize(( Integer ) value );
			break;
		case MULTICAST_8SIZE:
			configurator.setMulticastSize(( Integer )value );
			break;
		case MULTICAST_8INTERFACE:
			configurator.setMulticastInterface(( String )value );
			break;
		default:
			retval = false;
			break;
		}	
		return retval;
	}
}
