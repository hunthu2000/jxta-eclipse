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
package net.osgi.jp2p.jxta.advertisement;

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.jxta.advertisement.ModuleClassAdvertisementPropertySource.ModuleClassProperties;
import net.osgi.jp2p.jxta.advertisement.ModuleSpecAdvertisementPropertySource.ModuleSpecProperties;
import net.osgi.jp2p.jxta.advertisement.service.AdvertisementServicePropertySource.AdvertisementDirectives;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource.PeerGroupProperties;
import net.osgi.jp2p.jxta.pipe.PipePropertySource.PipeServiceProperties;
import net.osgi.jp2p.properties.AbstractPreferences;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class AdvertisementPreferences extends AbstractPreferences{

	public AdvertisementPreferences( IJp2pWritePropertySource<IJp2pProperties> source ) {
		super( source );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkPreferences#setPropertyFromString(net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties, java.lang.String)
	 */
	@Override
	public Object convertValue( IJp2pProperties id, String value ){
		if(!( isID( super.getSource(), id )))
			return value;
		try {
			return getCorrectID( super.getSource() );
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}	

	/**
	 * Create a default value if this is requested as attribute and adds it to the source if it is not present 
	 * @param id
	 * @return
	 */
	@Override
	public Object createDefaultValue( IJp2pProperties id ){
		if( ManagedProperty.isCreated( super.getSource().getManagedProperty(id)))
			return null;
		
		return null;
	}
	
	/**
	 * Get the correct id, based on the relevant id property. returns null if nothing was entered 
	 * @param source
	 * @return
	 * @throws URISyntaxException
	 */
	public static ID getCorrectID( IJp2pPropertySource<IJp2pProperties> source ) throws URISyntaxException{
		AdvertisementTypes type = AdvertisementTypes.valueOf( StringStyler.styleToEnum( (String) source.getDirective( AdvertisementDirectives.TYPE )));
		String str = null;
		switch( type ){
		case ADV:
			break;
		case MODULE_CLASS:
			str = (String) source.getProperty( ModuleClassProperties.MODULE_CLASS_ID );
			break;
		case MODULE_SPEC:
			str = (String) source.getProperty( ModuleSpecProperties.MODULE_SPEC_ID );
			break;
		case PEER:
			str = (String) source.getProperty( PeerGroupProperties.PEER_ID );
			break;
		case PEERGROUP:
			str = (String) source.getProperty( PeerGroupProperties.PEERGROUP_ID );
			break;
		case PIPE:
			str = (String) source.getProperty( PipeServiceProperties.PIPE_ID );
			break;
		}
		if( Utils.isNull(str))
			return null;
		return IDFactory.fromURI( new URI( str ));
	}

	/**
	 * Get the correct id, based on the relevant id property. returns null if nothing was entered 
	 * @param source
	 * @return
	 * @throws URISyntaxException
	 */
	public static boolean isID( IJp2pPropertySource<IJp2pProperties> source, IJp2pProperties id ){
		AdvertisementTypes type = AdvertisementTypes.valueOf( StringStyler.styleToEnum( (String) source.getDirective( AdvertisementDirectives.TYPE )));
		switch( type ){
		case ADV:
			break;
		case MODULE_CLASS:
			return ( ModuleClassProperties.MODULE_CLASS_ID.equals( id ));
		case MODULE_SPEC:
			return ( ModuleSpecProperties.MODULE_SPEC_ID.equals( id ) );
		case PEER:
			return ( PeerGroupProperties.PEER_ID.equals( id ) );
		case PEERGROUP:
			return ( PeerGroupProperties.PEERGROUP_ID.equals( id ) );
		case PIPE:
			return ( PipeServiceProperties.PIPE_ID.equals( id ) );
		}
		return false;
	}

}
