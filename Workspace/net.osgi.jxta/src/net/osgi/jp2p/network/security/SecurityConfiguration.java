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
package net.osgi.jp2p.network.security;

import java.net.URI;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jp2p.network.configurator.NetworkConfigurationFactory;
import net.osgi.jp2p.network.configurator.NetworkConfigurationPropertySource;
import net.osgi.jp2p.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;

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
		case SECURITY_8CERTFICATE:
			//source.setProperty( property, X509Certificate,( value ));
			retval = true;
			break;
		case SECURITY_8CERTIFICATE_CHAIN:
			//source.setProperty( property, X509Certificate[]( value ));
			retval = true;
			break;
		case SECURITY_8KEY_STORE_LOCATION:
			source.setProperty( property, URI.create( value ));
			retval = true;
			break;
		case SECURITY_8AUTHENTICATION_TYPE:
		case SECURITY_8PASSWORD:
		case SECURITY_8PRINCIPAL:
			source.setProperty( property, value );
			retval = true;
			break;
		case SECURITY_8PRIVATE_KEY:			
			//source.setProperty( property, new PrivateKey( value ));
			retval = true;
			break;
		default:
			break;
		}
		return retval;
	}	
}
