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
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jxse.network.http.Http2Preferences;
import net.osgi.jxse.network.http.HttpPreferences;
import net.osgi.jxse.network.multicast.MulticastPreferences;
import net.osgi.jxse.network.security.SecurityPreferences;
import net.osgi.jxse.network.tcp.TcpPreferences;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.properties.PartialPropertySource;
import net.osgi.jxse.seeds.ISeedListFactory;
import net.osgi.jxse.seeds.SeedListFactory;
import net.osgi.jxse.seeds.SeedListPropertySource;
import net.osgi.jxse.utils.StringStyler;

public class NetworkConfigurationFactory extends
		AbstractComponentFactory<NetworkConfigurator, NetworkConfiguratorProperties, IJxseDirectives> {

	public static final String S_NETWORK_CONFIGURATION_SERVICE = "NetworkConfigurationService";

	private Collection<ISeedListFactory> seedLists;
	
	private NetworkManagerFactory nmFactory;

	public NetworkConfigurationFactory( NetworkManagerFactory nmFactory, NetworkConfigurationPropertySource source ) {
		super( source );
		this.nmFactory = nmFactory;
		this.seedLists = new ArrayList<ISeedListFactory>();
	}

	public boolean addSeedlist( ISeedListFactory factory ){
		return this.seedLists.add( factory );
	}

	public boolean removeSeedlist( ISeedListFactory factory ){
		return this.seedLists.remove( factory );
	}

	@Override
	protected NetworkConfigurator onCreateModule( IJxsePropertySource<NetworkConfiguratorProperties, IJxseDirectives> properties) {
		NetworkConfigurator configurator = null;
		try {
			configurator = nmFactory.getModule().getConfigurator();
			URI home = (URI) super.getPropertySource().getProperty( NetworkConfiguratorProperties.HOME );
			if( home != null )
				configurator.setHome( new File( home ));
			configurator.clearRelaySeeds();
			configurator.clearRendezvousSeeds();
			for( ISeedListFactory factory: this.seedLists ){
				factory.setConfigurator(configurator);
				factory.createModule();
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
	
	protected void fillConfigurator( NetworkConfigurator configurator ) throws IOException{
		this.fillPartialConfigurator( configurator, super.getPropertySource());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fillPartialConfigurator( NetworkConfigurator configurator ,IJxsePropertySource<?,IJxseDirectives> source ) throws IOException{
		INetworkPreferences preferences;
		if( source instanceof PartialPropertySource ){
			preferences = getPreferences(( PartialPropertySource )source);
			preferences.fillConfigurator( configurator );
			return;
		}
		if( source instanceof SeedListPropertySource ){
			this.seedLists.add( new SeedListFactory((SeedListPropertySource) source ));
			return;
		}
		preferences = new OverviewPreferences( (IJxseWritePropertySource<NetworkConfiguratorProperties, IJxseDirectives>) source );
		preferences.fillConfigurator(configurator);
		for( IJxsePropertySource<?, ?> child: super.getPropertySource().getChildren() )
			this.fillPartialConfigurator( configurator, (IJxsePropertySource<NetworkConfiguratorProperties, IJxseDirectives>) child);
	}

	@Override
	protected void onParseDirectiveAfterCreation(IJxseDirectives directive,
			Object value) {
		if(!( directive instanceof Directives ))
			return;
		Directives dir = ( Directives )directive;
		switch( dir )
		{
		default:
			break;// TODO Auto-generated method stub
		}
		
	}

	/**
	 * Get the correct preferences for several services
	 * @param source
	 * @return
	 */
	public static INetworkPreferences getPreferences( PartialPropertySource<NetworkConfiguratorProperties,IJxseDirectives> source ){
		Components component = Components.valueOf( StringStyler.styleToEnum( source.getComponentName()));
		switch( component ){
		case TCP:
			return new TcpPreferences( source );
		case HTTP:
			return new HttpPreferences( source );
		case HTTP2:
			return new Http2Preferences( source );
		case MULTICAST:
			return new MulticastPreferences( source);
		case SECURITY:
			return new SecurityPreferences( source );
		default:
			return null;
		}
	}
}