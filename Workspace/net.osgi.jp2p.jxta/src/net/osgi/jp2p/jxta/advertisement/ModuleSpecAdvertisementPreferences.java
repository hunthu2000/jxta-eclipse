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
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.osgi.jp2p.jxta.advertisement.ModuleSpecAdvertisementPropertySource.ModuleSpecProperties;
import net.osgi.jp2p.properties.AbstractPreferences;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.utils.Utils;

public class ModuleSpecAdvertisementPreferences extends AbstractPreferences{

	private ModuleClassAdvertisement mcadv;
	
	public ModuleSpecAdvertisementPreferences( IJp2pWritePropertySource<IJp2pProperties> source, ModuleClassAdvertisement mcadv ) {
		super( source );
		this.mcadv = mcadv;
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkPreferences#setPropertyFromString(net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties, java.lang.String)
	 */
	@Override
	public Object convertValue( IJp2pProperties id, String value ){
		Object retval = this.convertPipeValue(id, value);
		if( retval != null )
			return retval;
		if( !( id instanceof ModuleSpecProperties ))
			return null;
		ModuleSpecProperties pid = ( ModuleSpecProperties )id;
		switch( pid ){
		case MODULE_SPEC_ID:
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
		if( !( id instanceof ModuleSpecProperties ))
			return null;
		ModuleSpecProperties msid = ( ModuleSpecProperties )id;
		switch( msid ){
		case MODULE_SPEC_ID:
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
		
		if( !( id instanceof ModuleSpecProperties ))
			return null;
		ModuleSpecProperties pid = ( ModuleSpecProperties )id;
		switch( pid ){
		case MODULE_SPEC_ID:
			return IDFactory.newModuleSpecID( mcadv.getModuleClassID() );
		default:
			break;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#getPeerID()
	 */
	public ModuleSpecID getModuleSpecID() throws URISyntaxException{
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
		ModuleSpecID pgId = (ModuleSpecID) createDefaultValue( ModuleSpecProperties.MODULE_SPEC_ID );
		ManagedProperty<IJp2pProperties, Object> property = source.getOrCreateManagedProperty( ModuleSpecProperties.MODULE_SPEC_ID, pgId.toString(), false );
		String str = (String) property.getValue();
		if( Utils.isNull(str)){
			str = pgId.toString();
			property.setValue(str);
		}
		URI uri = new URI( str );
		return (ModuleSpecID) IDFactory.fromURI( uri );
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkManagerPropertySource#setPeerID(net.jxta.peer.PeerID)
	 */
	public void setPipeID( ModuleSpecID mcid ){
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
		source.setProperty( ModuleSpecProperties.MODULE_SPEC_ID, mcid.toString() );
	}
	
}
