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

public class TcpConfiguration {

	private NetworkConfigurator configurator;
	private boolean publicAddressExclusive = false;
	
	public TcpConfiguration( NetworkConfigurator configurator ) {
		this.configurator = configurator;
	}

	public int getStartPort(){
		return this.configurator.getTcpStartPort();
	}

	public void setStartPort( int port ){
		this.configurator.setTcpStartPort( port );
	}

	public int getPort(){
		return this.configurator.getTcpPort();
	}

	public void setPort( int port ){
		this.configurator.setTcpPort( port );
	}

	public int getEndPort(){
		return this.configurator.getTcpEndport();
	}

	public void setEndPort( int port ){
		this.configurator.setTcpEndPort( port );
	}

	public boolean getIncomingStatus(){
		return this.configurator.getTcpIncomingStatus();
	}

	public void setIncomingStatus( boolean enabled ){
		this.configurator.setTcpIncoming( enabled );
	}

	public String getInterfaceAddress(){
		return this.configurator.getTcpInterfaceAddress();
	}

	public void setInterfaceAddress( String address ){
		this.configurator.setTcpInterfaceAddress(address);
	}

	public boolean getOutgoingStatus(){
		return this.configurator.getTcpOutgoingStatus();
	}

	public void setOutgoingStatus( boolean enabled ){
		this.configurator.setTcpOutgoing( enabled );
	}

	public boolean getPublicAddressExclusive(){
		return this.publicAddressExclusive;
	}

	public void setPublicAddressExclusive( boolean exclusive ){
		this.publicAddressExclusive =  exclusive;
	}

	public String getPublicAddress(){
		return this.configurator.getTcpPublicAddress();
	}

	public void setPublicAddress( String address ){
		this.configurator.setTcpPublicAddress(address, this.publicAddressExclusive);
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
		case TCP_8ENABLED:
		case TCP_8INCOMING_STATUS:
		case TCP_8OUTGOING_STATUS:
		case TCP_8PUBLIC_ADDRESS_EXCLUSIVE:
			factory.addProperty( property, Boolean.parseBoolean( value ));
			retval = true;
			break;
		case TCP_8END_PORT:
		case TCP_8PORT:
		case TCP_8START_PORT:
			factory.addProperty( property, Integer.parseInt( value ));
			retval = true;
			break;
		case TCP_8INTERFACE_ADDRESS:
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
			break;
		}	
	}
}
