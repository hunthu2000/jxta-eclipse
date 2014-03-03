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
package net.jp2p.jxta.network.tcp;

import java.util.Iterator;

import net.jp2p.container.partial.PartialPropertySource;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.jxta.network.INetworkPreferences;
import net.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.jxta.compatibility.platform.NetworkConfigurator;

public class TcpPreferences implements INetworkPreferences {

	public static final String S_TCP_CONFIGURATION = "Tcp Configuration";
	
	private PartialPropertySource source;
	
	public TcpPreferences( PartialPropertySource source ) {
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
		case TCP_8ENABLED:
			configurator.setTcpEnabled((boolean) value );
			break;
		case TCP_8INCOMING_STATUS:
			configurator.setTcpIncoming((boolean) value );
			break;
		case TCP_8OUTGOING_STATUS:
			configurator.setTcpOutgoing((boolean)value );
			break;
		case TCP_8PUBLIC_ADDRESS_EXCLUSIVE:
			configurator.setTcpPublicAddress(( String )value, true );
			break;
		case TCP_8END_PORT:
			configurator.setTcpEndPort(( Integer ) value );
			break;
		case TCP_8PORT:
			configurator.setTcpPort(( Integer ) value );
			break;
		case TCP_8START_PORT:
			configurator.setTcpStartPort(( Integer )value );
			break;
		case TCP_8INTERFACE_ADDRESS:
			configurator.setTcpInterfaceAddress(( String )value );
			break;
		case TCP_8PUBLIC_ADDRESS:
			configurator.setTcpPublicAddress(( String) value, false );
			break;			
		default:
			retval = false;
			break;
		}	
		return retval;
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkPreferences#setPropertyFromString(net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties, java.lang.String)
	 */
	public Object convertTo( IJp2pProperties id, String value ){
		Object val = convertStringToCorrectType( id, value);
		source.setProperty( id, val );
		return val;
	}
	
	/**
	 * Convert a given string value to the correct type
	 * @param source
	 * @param property
	 * @param value
	 */
	public static Object convertStringToCorrectType( IJp2pProperties property, String value ){
		if(!(property instanceof NetworkConfiguratorProperties ))
			return null;
		NetworkConfiguratorProperties id = (NetworkConfiguratorProperties) property;
		switch( id ){
		case TCP_8INTERFACE_ADDRESS:
		case TCP_8PUBLIC_ADDRESS:
		case TCP_8PUBLIC_ADDRESS_EXCLUSIVE:
			return value;
		case TCP_8ENABLED:
		case TCP_8INCOMING_STATUS:
		case TCP_8OUTGOING_STATUS:
			return Boolean.parseBoolean( value );
		case TCP_8END_PORT:
		case TCP_8PORT:
		case TCP_8START_PORT:
			return Integer.parseInt( value );
		default:
			return null;
		}	
	}
}
