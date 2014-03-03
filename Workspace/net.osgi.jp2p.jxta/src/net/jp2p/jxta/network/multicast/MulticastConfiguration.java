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
package net.jp2p.jxta.network.multicast;

import net.jp2p.jxta.network.configurator.NetworkConfigurationFactory;
import net.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource;
import net.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.jxta.compatibility.platform.NetworkConfigurator;

public class MulticastConfiguration {

	public static final String S_MULTICAST_CONFIGURATION = "Multicast Configuration";
	
	private NetworkConfigurator configurator;
	
	public MulticastConfiguration( NetworkConfigurator configurator ) {
		this.configurator = configurator;
	}

	public boolean isUseMulticastStatus(){
		return this.configurator.getMulticastStatus();
	}

	public void setUseMulticast( boolean enabled ){
		this.configurator.setUseMulticast( enabled );
	}

	public String getMulticastAddress(){
		return this.configurator.getMulticastAddress();
	}

	public void setMulticastAdress( String address ){
		this.configurator.setMulticastAddress( address);
	}

	public String getMulticastInterface(){
		return this.configurator.getMulticastInterface();
	}
	public void setMulticastInterface( String interfaceAddress ){
		this.configurator.setMulticastInterface( interfaceAddress );
	}

	public int getMulticastPoolSize(){
		return this.configurator.getMulticastPoolSize();
	}

	public void setMulticastInterface( int poolSize ){
		this.configurator.setMulticastPoolSize( poolSize );
	}

	public int getMulticastPort(){
		return this.configurator.getMulticastPort();
	}

	public void setMulticastPort( int port ){
		this.configurator.setMulticastPort( port );
	}

	public int getMulticastSize(){
		return this.configurator.getMulticastSize();
	}

	public void setMulticastSize( int size ){
		this.configurator.setMulticastSize( size );
	}

	public boolean isUseOnlyRelaySeedsStatus(){
		return this.configurator.getUseOnlyRelaySeedsStatus();
	}

	public void setUseOnlyRelaySeedsStatus( boolean enabled ){
		this.configurator.setUseOnlyRelaySeeds( enabled );
	}

	public boolean isUseOnlyRendezvousSeedsStatus(){
		return this.configurator.getUseOnlyRendezvousSeedsStatus();
	}

	public void setUseOnlyRendezvousSeedsStatus( boolean enabled ){
		this.configurator.setUseOnlyRendezvousSeeds( enabled );
	}

		
	/**
	 * Create the correct type for the given property
	 * @param factory
	 * @param property
	 * @param value
	 */
	public static boolean addStringProperty( NetworkConfigurationFactory factory, NetworkConfiguratorProperties property, String value ){
		boolean retval = false;
		NetworkConfigurationPropertySource source = (NetworkConfigurationPropertySource) factory.getPropertySource();
		switch( property ){
		case MULTICAST_8ENABLED:
			source.setProperty( property, Boolean.parseBoolean( value ));
			retval = true;
			break;
		case MULTICAST_8POOL_SIZE:
		case MULTICAST_8PORT:
		case MULTICAST_8SIZE:
			source.setProperty( property, Integer.parseInt( value ));
			retval = true;
			break;
		case MULTICAST_8ADDRESS:
		case MULTICAST_8STATUS:
		case MULTICAST_8INTERFACE:
			source.setProperty( property, value );
			retval = true;
			break;
		default:
			break;
		}	
		return retval;
	}	

	/**
	 * Fill the configurator with the given properties from a key string
	 * @param configurator
	 * @param property
	 * @param value
	 */
	public static boolean fillConfigurator( NetworkConfigurator configurator, String key, Object value ){
		NetworkConfiguratorProperties id = NetworkConfiguratorProperties.convertTo(key);
		if( id == null )
			return false;
		return fillConfigurator(configurator, id, value);	
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
