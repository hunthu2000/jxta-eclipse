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

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jxse.network.NetworkConfigurationFactory.NetworkConfiguratorProperties;

public class UseConfiguration {

	private NetworkConfigurator configurator;
	
	public UseConfiguration( NetworkConfigurator configurator ) {
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
		switch( property ){
		case USE_MULTICAST:
		case USE_ONLY_RELAY_SEEDS:
		case USE_ONLY_RENDEZVOUS_SEEDS:
			factory.addProperty( property, Boolean.parseBoolean( value ));
			retval = true;
			break;
		case MULTICAST_8POOL_SIZE:
		case MULTICAST_8PORT:
		case MULTICAST_8SIZE:
			factory.addProperty( property, Integer.parseInt( value ));
			retval = true;
			break;
		case MULTICAST_8ADDRESS:
		case MULTICAST_8STATUS:
		case MULTICAST_8INTERFACE:
			factory.addProperty( property, value );
			retval = true;
			break;
		default:
			break;
		}	
		return retval;
	}	

	/**
	 * Fill the configurator with the given properties
	 * @param configurator
	 * @param property
	 * @param value
	 */
	public static void fillConfigurator( NetworkConfigurator configurator, NetworkConfiguratorProperties property, Object value ){
		switch( property ){
		case USE_MULTICAST:
		case MULTICAST_8STATUS:
			configurator.setUseMulticast((boolean) value );
			break;
		case USE_ONLY_RELAY_SEEDS:
			configurator.setUseOnlyRelaySeeds((boolean) value );
			break;
		case USE_ONLY_RENDEZVOUS_SEEDS:
			configurator.setUseOnlyRendezvousSeeds((boolean)value );
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
			break;
		}	
	}
}
