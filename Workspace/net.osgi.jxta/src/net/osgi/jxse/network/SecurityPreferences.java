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

import java.net.URI;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.PartialPropertySource;

public class SecurityPreferences implements INetworkPreferences{

	public static final String S_SCURITY_CONFIGURATION = "Security Configuration";
	
	private PartialPropertySource<NetworkConfiguratorProperties, IJxseDirectives> source;
	
	public SecurityPreferences( PartialPropertySource<NetworkConfiguratorProperties, IJxseDirectives> source ) {
		this.source = source;
	}
	
	@Override
	public String getName() {
		return this.source.getComponentName();
	}

	@Override
	public boolean setPropertyFromString(NetworkConfiguratorProperties id, String value) {
		source.setProperty(id, convertStringToCorrectType(id, value));
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
		case SECURITY_8AUTHENTICATION_TYPE:
			configurator.setAuthenticationType((String) value );
			break;
		case SECURITY_8CERTFICATE:
			configurator.setCertificate(( X509Certificate ) value );
			break;
		case SECURITY_8CERTIFICATE_CHAIN:
			configurator.setCertificateChain(( X509Certificate[] )value );
			break;
		case SECURITY_8KEY_STORE_LOCATION:
			configurator.setKeyStoreLocation(( URI )value );
			break;
		case SECURITY_8PASSWORD:
			configurator.setPassword(( String ) value );
			break;
		case SECURITY_8PRINCIPAL:
			configurator.setPrincipal(( String ) value );
			break;
		case SECURITY_8PRIVATE_KEY:
			configurator.setPrivateKey(( PrivateKey )value );
			break;
		default:
			retval = false;
			break;
		}	
		return retval;
	}

	/**
	 * Convert a given string value to the correct type
	 * @param source
	 * @param property
	 * @param value
	 */
	public static Object convertStringToCorrectType( NetworkConfiguratorProperties property, String value ){
		switch( property ){
		case SECURITY_8AUTHENTICATION_TYPE:
		case SECURITY_8PASSWORD:
		case SECURITY_8PRINCIPAL:
			return value;
		case SECURITY_8PRIVATE_KEY:
			return value;
		case SECURITY_8CERTFICATE:
			return value;
		case SECURITY_8CERTIFICATE_CHAIN:
			return value;
		case SECURITY_8KEY_STORE_LOCATION:
			return URI.create(value);
		default:
			return null;
		}	
	}
}