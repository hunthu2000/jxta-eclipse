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
package net.osgi.jp2p.registration;

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.peer.PeerID;
import net.osgi.jp2p.discovery.DiscoveryPropertySource.DiscoveryMode;
import net.osgi.jp2p.properties.AbstractPreferences;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.registration.RegistrationPropertySource.RegistrationProperties;

public class RegistrationPreferences extends AbstractPreferences<RegistrationProperties>
{
	public RegistrationPreferences( IJp2pWritePropertySource<RegistrationProperties> source )
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
	public Object convertValue( RegistrationProperties id, String value ){
		IJp2pWritePropertySource<RegistrationProperties> source = super.getSource();
		switch( id ){
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
