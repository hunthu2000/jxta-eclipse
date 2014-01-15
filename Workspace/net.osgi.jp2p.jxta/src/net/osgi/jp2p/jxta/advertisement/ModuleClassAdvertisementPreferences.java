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

import net.jxta.id.IDFactory;
import net.jxta.platform.ModuleClassID;
import net.osgi.jp2p.jxta.advertisement.ModuleClassAdvertisementPropertySource.ModuleClassProperties;
import net.osgi.jp2p.properties.AbstractPreferences;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.ManagedProperty;

public class ModuleClassAdvertisementPreferences extends AbstractPreferences{

	public ModuleClassAdvertisementPreferences( IJp2pWritePropertySource<IJp2pProperties> source ) {
		super( source );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkPreferences#setPropertyFromString(net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties, java.lang.String)
	 */
	@Override
	public Object convertValue( IJp2pProperties id, String value ){
		Object retval = this.convertPipeValue(id, value);
		if( retval != null )
			return retval;
		if( !( id instanceof ModuleClassProperties ))
			return null;
		ModuleClassProperties pid = ( ModuleClassProperties )id;
		switch( pid ){
		case MODULE_CLASS_ID:
			URI uri = URI.create(value);
			try {
				return IDFactory.fromURI( uri );
			} catch (URISyntaxException e) {
				throw new RuntimeException( e );
			}
		default:
			break;
		}
		return null;
	}	

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkPreferences#setPropertyFromString(net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties, java.lang.String)
	 */
	protected Object convertPipeValue( IJp2pProperties id, String value ){
		if( !( id instanceof ModuleClassProperties ))
			return null;
		ModuleClassProperties msid = ( ModuleClassProperties )id;
		switch( msid ){
		case MODULE_CLASS_ID:
			URI uri = URI.create(value);
			try {
				return IDFactory.fromURI( uri );
			} catch (URISyntaxException e) {
				throw new RuntimeException( e );
			}
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
	public Object createDefaultValue( IJp2pProperties id ){
		if( ManagedProperty.isCreated( super.getSource().getManagedProperty(id)))
			return null;
		
		if( !( id instanceof ModuleClassProperties ))
			return null;
		ModuleClassProperties pid = ( ModuleClassProperties )id;
		switch( pid ){
		case MODULE_CLASS_ID:
			return IDFactory.newModuleClassID();
		default:
			break;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#getPeerID()
	 */
	public ModuleClassID getModuleClassID() throws URISyntaxException{
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
		ModuleClassID pgId = (ModuleClassID) createDefaultValue( ModuleClassProperties.MODULE_CLASS_ID );
		ManagedProperty<IJp2pProperties, Object> property = source.getOrCreateManagedProperty( ModuleClassProperties.MODULE_CLASS_ID, pgId.toString(), false );
		String str = (String) property.getValue();
		URI uri = new URI( str );
		return (ModuleClassID) IDFactory.fromURI( uri );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setPeerID(net.jxta.peer.PeerID)
	 */
	public void setPipeID( ModuleClassID mcid ){
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
		source.setProperty( ModuleClassProperties.MODULE_CLASS_ID, mcid.toString() );
	}
	
}
