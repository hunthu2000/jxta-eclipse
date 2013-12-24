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
package net.osgi.jxse.advertisement;

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.id.IDFactory;
import net.jxta.pipe.PipeService;
import net.osgi.jxse.pipe.PipePropertySource.PipeProperties;
import net.osgi.jxse.properties.AbstractPreferences;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.utils.StringStyler;

public class AdvertisementPreferences extends AbstractPreferences<IJxseProperties>{

	public enum PipeServiceTypes{
		UNICAST,
		SECURE_UNICAST,
		PROPAGATE;
		
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
		
		/**
		 * Convert the enum to a form that the jxta lib can understand
		 * @param pipeType
		 * @return
		 */
		public static String convert( PipeServiceTypes pipeType ){
			switch( pipeType ){
			case UNICAST:
				return PipeService.UnicastType;
			case SECURE_UNICAST:
				return PipeService.UnicastSecureType;
			default:
				return PipeService.PropagateType;
			}
		}
	}

	public AdvertisementPreferences( IJxseWritePropertySource<IJxseProperties> source ) {
		super( source );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkPreferences#setPropertyFromString(net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties, java.lang.String)
	 */
	@Override
	public Object convertValue( IJxseProperties id, String value ){
		if( !( id instanceof PipeProperties ))
			return null;
		PipeProperties pid = ( PipeProperties )id;
		switch( pid ){
		case PIPE_ID:
			URI uri = URI.create(value);
			try {
				return IDFactory.fromURI( uri );
			} catch (URISyntaxException e) {
				throw new RuntimeException( e );
			}
		case TYPE:
			return PipeServiceTypes.valueOf( value );
		default:
			break;
		}
		return null;
	}	

	/**
	 * Create a default value if this is requested as attribute and adds it to the source if it is not present 
	 * @param id
	 * @return
	 */
	@Override
	public Object createDefaultValue( IJxseProperties id ){
		if( !ManagedProperty.isCreated( super.getSource().getManagedProperty(id)))
			return null;
		
		Object value = null;
		if( !( id instanceof PipeProperties ))
			return null;
		PipeProperties pid = ( PipeProperties )id;
		switch( pid ){
		case PIPE_ID:
			value = null;//IDFactory.newPipeID( provider.getPeerGroup().getPeerGroupID() );
			break;
		default:
			break;
		}
		if( value != null )
			super.getSource().getOrCreateManagedProperty(id, value, false );
		return null;
	}
}
