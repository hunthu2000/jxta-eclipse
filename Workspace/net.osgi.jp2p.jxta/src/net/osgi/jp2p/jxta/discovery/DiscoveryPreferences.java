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
package net.osgi.jp2p.jxta.discovery;

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.peer.PeerID;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.properties.AbstractPreferences;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class DiscoveryPreferences extends AbstractPreferences<IJp2pProperties>
{
	public DiscoveryPreferences( IJp2pWritePropertySource<IJp2pProperties> source )
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
	public Object convertValue( IJp2pProperties key, String value ){
		if(!( key instanceof DiscoveryProperties ))
			return null;
		DiscoveryProperties id = (DiscoveryProperties) key;
		switch( id ){
		case ADVERTISEMENT_TYPE:
			return AdvertisementTypes.valueOf( StringStyler.styleToEnum(value));
		case ATTRIBUTE:
		case WILDCARD:
			return value;
		case COUNT:
		case THRESHOLD:
		case WAIT_TIME:
			return Integer.valueOf( value);
		case PEER_ID:
			return PeerID.create( URI.create( value ));
		default:
			return null;
		}
	}
}
