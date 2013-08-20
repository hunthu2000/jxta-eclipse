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

public class HttpConfiguration {

	private NetworkConfigurator configurator;
	private boolean publicAddressExclusive = false;
	
	public HttpConfiguration( NetworkConfigurator configurator ) {
		this.configurator = configurator;
	}

	public int getStartPort(){
		return this.configurator.getHttp2StartPort();
	}

	public void setStartPort( int port ){
		this.configurator.setHttp2StartPort( port );
	}

	public int getPort(){
		return this.configurator.getHttpPort();
	}

	public void setPort( int port ){
		this.configurator.setHttp2Port( port );
	}

	public int getEndPort(){
		return this.configurator.getHttp2EndPort();
	}

	public void setEndPort( int port ){
		this.configurator.setHttp2EndPort( port );
	}

	public boolean getIncomingStatus(){
		return this.configurator.getHttp2IncomingStatus();
	}

	public void setIncomingStatus( boolean enabled ){
		this.configurator.setHttp2Incoming( enabled );
	}

	public String getInterfaceAddress(){
		return this.configurator.getHttp2InterfaceAddress();
	}

	public void setInterfaceAddress( String address ){
		this.configurator.setHttp2InterfaceAddress(address);
	}

	public boolean getHttpOutgoingStatus(){
		return this.configurator.getHttp2OutgoingStatus();
	}

	public void setOutgoingStatus( boolean enabled ){
		this.configurator.setHttp2Outgoing( enabled );
	}

	public boolean getPublicAddressExclusive(){
		return this.publicAddressExclusive;
	}

	public void setPublicAddressExclusive( boolean exclusive ){
		this.publicAddressExclusive =  exclusive;
	}

	public String getPublicAddress(){
		return this.configurator.getHttp2PublicAddress();
	}

	public void setHttpPublicAddress( String address ){
		this.configurator.setHttp2PublicAddress(address, this.publicAddressExclusive);
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
		case HTTP_8ENABLED:
		case HTTP_8INCOMING_STATUS:
		case HTTP_8OUTGOING_STATUS:
		case HTTP_8TO_PUBLIC_ADDRESS_EXCLUSIVE:
			factory.addProperty( property, Boolean.parseBoolean( value ));
			retval = true;
			break;
		case HTTP_8END_PORT:
		case HTTP_8PORT:
		case HTTP_8START_PORT:
			factory.addProperty( property, Integer.parseInt( value ));
			retval = true;
			break;
		case HTTP_8PUBLIC_ADDRESS_EXCLUSIVE:
		case HTTP_8PUBLIC_ADDRESS:
		case HTTP_8INTERFACE_ADDRESS:	
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
		case HTTP_8ENABLED:
			configurator.setHttp2Enabled((boolean) value );
			break;
		case HTTP_8INCOMING_STATUS:
			configurator.setHttp2Incoming((boolean) value );
			break;
		case HTTP_8OUTGOING_STATUS:
			configurator.setHttp2Outgoing((boolean)value );
			break;
		case HTTP_8PUBLIC_ADDRESS_EXCLUSIVE:
			configurator.setHttp2PublicAddress(( String )value, true );
			break;
		case HTTP_8END_PORT:
			configurator.setHttp2EndPort(( Integer ) value );
			break;
		case HTTP_8PORT:
			configurator.setHttpPort(( Integer ) value );
			break;
		case HTTP_8START_PORT:
			configurator.setHttp2StartPort(( Integer )value );
			break;
		case HTTP_8INTERFACE_ADDRESS:
			configurator.setHttpInterfaceAddress(( String )value );
			break;
		case HTTP_8PUBLIC_ADDRESS:
			configurator.setHttpPublicAddress(( String) value, false );
			break;			
		default:
			break;
		}	
	}

}
