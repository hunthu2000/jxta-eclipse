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
package net.osgi.jxse.discovery;

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.peer.PeerID;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jxse.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jxse.properties.AbstractPreferences;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.utils.StringStyler;

public class DiscoveryPreferences extends AbstractPreferences<IJxseProperties>
{
	public DiscoveryPreferences( IJxseWritePropertySource<IJxseProperties> source )
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
	public Object convertValue( IJxseProperties key, String value ){
		if(!( key instanceof DiscoveryProperties ))
			return null;
		DiscoveryProperties id = (DiscoveryProperties) key;
		switch( id ){
		case ADVERTISEMENT_TYPE:
			return AdvertisementTypes.valueOf( StringStyler.styleToEnum(value));
		case ATTRIBUTE:
		case WILDCARD:
			return value;
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
