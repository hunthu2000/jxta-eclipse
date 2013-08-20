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
package net.osgi.jxta.network;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jxta.factory.AbstractServiceComponentFactory;
import net.osgi.jxta.preferences.IJxtaPreferences;
import net.osgi.jxta.seeds.ISeedListFactory;
import net.osgi.jxta.utils.StringStyler;

public class NetworkConfigurationFactory extends
		AbstractServiceComponentFactory<NetworkConfigurator> {

	public enum NetworkConfiguratorProperties{
		AUTHENTICATION_TYPE,
		CERTFICATE,
		CERTIFICATE_CHAIN,
		DESCRIPTION,
		HOME,
		HTTP_8ENABLED,
		HTTP_8START_PORT,
		HTTP_8END_PORT,
		HTTP_8INCOMING_STATUS,
		HTTP_8INTERFACE_ADDRESS,
		HTTP_8OUTGOING_STATUS,
		HTTP_8PORT,
		HTTP_8PUBLIC_ADDRESS,
		HTTP_8TO_PUBLIC_ADDRESS_EXCLUSIVE,
		HTTP_8PUBLIC_ADDRESS_EXCLUSIVE,
		INFRASTRUCTURE_8NAME,
		INFRASTRUCTURE_8DESCRIPTION,
		INFRASTRUCTURE_8ID,
		KEY_STORE_LOCATION,
		MODE,
		MULTICAST_8ADDRESS,
		MULTICAST_8INTERFACE,
		MULTICAST_8POOL_SIZE,
		MULTICAST_8PORT,
		MULTICAST_8SIZE,
		MULTICAST_8STATUS,
		NAME,
		PASSWORD,
		PEER_ID,
		PRINCIPAL,
		PRIVATE_KEY,
		RELAY_8MAX_CLIENTS,
		RELAY_8SEEDING_URIS,
		RELAY_8SEED_URIS,
		RENDEZVOUS_8MAX_CLIENTS,
		RENDEZVOUS_8SEEDING_URIS,
		RENDEZVOUS_8SEED_URIS,
		STORE_HOME,
		TCP_8ENABLED,
		TCP_8END_PORT,
		TCP_8INCOMING_STATUS,
		TCP_8INTERFACE_ADDRESS,
		TCP_8OUTGOING_STATUS,
		TCP_8PORT,
		TCP_8PUBLIC_ADDRESS,
		TCP_8PUBLIC_ADDRESS_EXCLUSIVE,
		TCP_8START_PORT,
		USE_MULTICAST,
		USE_ONLY_RELAY_SEEDS,
		USE_ONLY_RENDEZVOUS_SEEDS;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}	
		
		public static NetworkConfiguratorProperties convertTo( String str ){
			String enumStr = StringStyler.styleToEnum( str );
			return valueOf( enumStr );
		}
	}

	public static final String S_NETWORK_CONFIGURATION_SERVICE = "NetworkConfigurationService";

	private IJxtaPreferences preferences;
	private Collection<ISeedListFactory> seedLists;
	
	private NetworkManagerFactory nmFactory;

	public NetworkConfigurationFactory( NetworkManagerFactory nmFactory ) {
		super( Components.NETWORK_CONFIGURATOR, false );
		this.nmFactory = nmFactory;
		this.preferences = nmFactory.getPreferences();
		this.seedLists = new ArrayList<ISeedListFactory>();
		super.addDirective( Directives.CREATE_PARENT, "true" );
		this.fillDefaultValues();
	}

	@Override
	protected void fillDefaultValues() {
		
		if( this.preferences == null)
			return;
		try {
			this.addProperty( NetworkConfiguratorProperties.PEER_ID, preferences.getPeerID());
			this.addProperty( NetworkConfiguratorProperties.NAME, preferences.getIdentifier() );
			this.addProperty( NetworkConfiguratorProperties.HOME, preferences.getHomeFolder() );
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addProperty(Object key, Object value) {
		if(!( key instanceof NetworkConfiguratorProperties) || ( value == null ))
			return;
		if( value instanceof String ){
			if( TcpConfiguration.addStringProperty(this, (NetworkConfiguratorProperties)key, (String )value))
				return;
			if( HttpConfiguration.addStringProperty(this, (NetworkConfiguratorProperties)key, (String )value))
				return;
			if( UseConfiguration.addStringProperty(this, (NetworkConfiguratorProperties)key, (String )value))
				return;
		}
		super.addProperty(key, value);
	}

	public boolean addSeedlist( ISeedListFactory factory ){
		return this.seedLists.add( factory );
	}

	public boolean removeSeedlist( ISeedListFactory factory ){
		return this.seedLists.remove( factory );
	}

	@Override
	protected void onParseDirectivePriorToCreation(Directives directive,
			String value) {
	}

	@Override
	protected void onParseDirectiveAfterCreation( NetworkConfigurator module, Directives directive, String value) {
	}

	@Override
	protected NetworkConfigurator onCreateModule() {
		NetworkConfigurator configurator = null;
		try {
			configurator = nmFactory.getModule().getConfigurator();
			URI home = (URI)super.getProperty( NetworkConfiguratorProperties.HOME );
			if( home != null )
				configurator.setHome( new File( home ));
			configurator.clearRelaySeeds();
			configurator.clearRendezvousSeeds();
			for( ISeedListFactory factory: this.seedLists )
				try {
					factory.createSeedlist( configurator );
				} catch (IOException e) {
					Logger log = Logger.getLogger( this.getClass().getName() );
					log.log( Level.SEVERE, e.getMessage() );
					e.printStackTrace();
				}
			this.fillConfigurator(configurator);
			configurator.save();
		} catch (IOException e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
			return null;
		}
		return configurator;
	}
	
	protected void fillConfigurator( NetworkConfigurator configurator ){
		Map<Object, Object> properties = super.getProperties();
		for( Object property: properties.keySet() ){
			if(!( property instanceof NetworkConfiguratorProperties ))
				continue;
			NetworkConfiguratorProperties key = ( NetworkConfiguratorProperties )property;
			Object value = super.getProperty(key); 
			TcpConfiguration.fillConfigurator(configurator, key,  value );
			HttpConfiguration.fillConfigurator(configurator, key, value );
			UseConfiguration.fillConfigurator(configurator, key, value);
		}
	}
}