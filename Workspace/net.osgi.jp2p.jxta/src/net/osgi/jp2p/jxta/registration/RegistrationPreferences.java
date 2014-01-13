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
package net.osgi.jp2p.jxta.registration;

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.peer.PeerID;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource.DiscoveryMode;
import net.osgi.jp2p.jxta.registration.RegistrationPropertySource.RegistrationProperties;
import net.osgi.jp2p.properties.AbstractPreferences;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class RegistrationPreferences extends AbstractPreferences
{
	public RegistrationPreferences( IJp2pWritePropertySource<IJp2pProperties> source )
	{
		super( source );
	}
	
	/**
	 * Get the correct property from the given string
	 * @param property
	 * @param value
	 * @return
	 * @throws URISyntaxException
	 */
	@Override
	public Object convertValue( IJp2pProperties id, String value ){
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
		if(!( id instanceof IJp2pProperties ))
			return null;
		RegistrationProperties key = (RegistrationProperties) id;
		switch( key ){
		case ATTRIBUTE:
		case WILDCARD:
			return source.setProperty(id, value);
		case THRESHOLD:
		case WAIT_TIME:
			return source.setProperty(id, Integer.valueOf( value));
		case DISCOVERY_MODE:
			return source.setProperty(id, DiscoveryMode.valueOf( value ));
		case PEER_ID:
			return source.setProperty(id, PeerID.create( URI.create( value )));
		default:
			return false;
		}
	}
}
