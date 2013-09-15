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

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;

public class SecurityConfiguration {

	public static final String S_HTTP_CONFIGURATION = "Security Configuration";

	private NetworkConfigurator configurator;
	
	public SecurityConfiguration( NetworkConfigurator configurator ) {
		this.configurator = configurator;
	}

	public String getAuthenticationType(){
		return this.configurator.getAuthenticationType();
	}

	public void setAuthenticationType( String type ){
		this.configurator.setAuthenticationType( type );
	}

	public X509Certificate getCertificate(){
		return this.configurator.getCertificate();
	}

	public void setCertificate( X509Certificate certificate ){
		this.configurator.setCertificate(certificate);
	}

	public X509Certificate[] getCertificateChain(){
		return this.configurator.getCertificateChain();
	}

	public void setCertificateChain( X509Certificate[] chain ){
		this.configurator.setCertificateChain(chain);
	}

	public URI getKeyStoreLocation(){
		return this.configurator.getKeyStoreLocation();
	}

	public void setKeyStoreLocation( URI location ){
		this.configurator.setKeyStoreLocation( location );
	}

	public String getPassword(){
		return this.configurator.getPassword();
	}

	public void setPassword( String password ){
		this.configurator.setPassword(password);
	}

	public PrivateKey getPrivateKey(){
		return this.configurator.getPrivateKey();
	}

	public void setPrivateKey( PrivateKey key ){
		this.configurator.setPrivateKey( key );
	}

	public String getPrincipal(){
		return this.configurator.getPrincipal();
	}

	public void setPrincipal( String principal ){
		this.configurator.setPrincipal(principal);
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
		case CERTFICATE:
			//source.setProperty( property, X509Certificate,( value ));
			retval = true;
			break;
		case CERTIFICATE_CHAIN:
			//source.setProperty( property, X509Certificate[]( value ));
			retval = true;
			break;
		case KEY_STORE_LOCATION:
			source.setProperty( property, URI.create( value ));
			retval = true;
			break;
		case AUTHENTICATION_TYPE:
		case PASSWORD:
		case PRINCIPAL:
			source.setProperty( property, value );
			retval = true;
			break;
		case PRIVATE_KEY:			
			//source.setProperty( property, new PrivateKey( value ));
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
		case AUTHENTICATION_TYPE:
			configurator.setAuthenticationType((String) value );
			break;
		case CERTFICATE:
			configurator.setCertificate(( X509Certificate ) value );
			break;
		case CERTIFICATE_CHAIN:
			configurator.setCertificateChain(( X509Certificate[] )value );
			break;
		case KEY_STORE_LOCATION:
			configurator.setKeyStoreLocation(( URI )value );
			break;
		case PASSWORD:
			configurator.setPassword(( String ) value );
			break;
		case PRINCIPAL:
			configurator.setPrincipal(( String ) value );
			break;
		case PRIVATE_KEY:
			configurator.setPrivateKey(( PrivateKey )value );
			break;
		default:
			break;
		}	
	}

}