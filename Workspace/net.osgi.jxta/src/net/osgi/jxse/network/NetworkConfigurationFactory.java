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
package net.osgi.jxse.network;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Directives;
import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jxse.seeds.ISeedListFactory;

public class NetworkConfigurationFactory extends
		AbstractComponentFactory<NetworkConfigurator, NetworkConfiguratorProperties, Directives> {

	public static final String S_NETWORK_CONFIGURATION_SERVICE = "NetworkConfigurationService";

	private Collection<ISeedListFactory> seedLists;
	
	private NetworkManagerFactory nmFactory;

	public NetworkConfigurationFactory( NetworkManagerFactory nmFactory ) {
		super( null );//nmFactory.getPropertySource() );
		this.nmFactory = nmFactory;
		this.seedLists = new ArrayList<ISeedListFactory>();
		super.addDirective( Directives.CREATE_PARENT, "true" );
		this.fillDefaultValues();
	}

	protected void fillDefaultValues() {	
		/*
		try {
			this.addProperty( NetworkConfiguratorProperties.PEER_ID, preferences.getPeerID());
			this.addProperty( NetworkConfiguratorProperties.NAME, preferences.getIdentifier() );
			this.addProperty( NetworkConfiguratorProperties.HOME, preferences.getHomeFolder() );
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		*/
	}

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
		//super.addProperty(key, value);
	}

	public boolean addSeedlist( ISeedListFactory factory ){
		return this.seedLists.add( factory );
	}

	public boolean removeSeedlist( ISeedListFactory factory ){
		return this.seedLists.remove( factory );
	}

	@Override
	protected void onParseDirectivePriorToCreation(Directives directive, Object value) {
	}

	@Override
	protected void onParseDirectiveAfterCreation( NetworkConfigurator module, Directives directive, Object value) {
	}

	@Override
	protected NetworkConfigurator onCreateModule() {
		NetworkConfigurator configurator = null;
		try {
			configurator = nmFactory.getModule().getConfigurator();
			URI home = (URI)super.getPropertySource().getProperty( NetworkConfiguratorProperties.HOME );
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
		Iterator<NetworkConfiguratorProperties> properties = super.getPropertySource().propertyIterator();
		while( properties.hasNext() ){
			NetworkConfiguratorProperties property = properties.next();
			if(!( property instanceof NetworkConfiguratorProperties ))
				continue;
			NetworkConfiguratorProperties key = ( NetworkConfiguratorProperties )property;
			Object value = super.getPropertySource().getProperty( key); 
			TcpConfiguration.fillConfigurator(configurator, key,  value );
			HttpConfiguration.fillConfigurator(configurator, key, value );
			UseConfiguration.fillConfigurator(configurator, key, value);
		}
	}
}