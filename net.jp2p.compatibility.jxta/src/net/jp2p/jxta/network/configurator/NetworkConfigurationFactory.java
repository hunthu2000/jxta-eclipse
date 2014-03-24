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
package net.jp2p.jxta.network.configurator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.factory.AbstractDependencyFactory;
import net.jp2p.container.factory.ComponentBuilderEvent;
import net.jp2p.container.factory.IComponentFactory;
import net.jp2p.container.partial.PartialPropertySource;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.utils.StringStyler;
import net.jp2p.jxta.factory.IJxtaComponents.JxtaCompatComponents;
import net.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.jp2p.jxta.network.INetworkPreferences;
import net.jp2p.jxta.network.NetworkManagerPropertySource;
import net.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource;
import net.jp2p.jxta.network.configurator.OverviewPreferences;
import net.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.jp2p.jxta.network.http.Http2Preferences;
import net.jp2p.jxta.network.http.HttpPreferences;
import net.jp2p.jxta.network.multicast.MulticastPreferences;
import net.jp2p.jxta.network.security.SecurityPreferences;
import net.jp2p.jxta.network.tcp.TcpPreferences;
import net.jp2p.jxta.seeds.SeedListFactory;
import net.jp2p.jxta.seeds.SeedListPropertySource;
import net.jxta.compatibility.platform.NetworkConfigurator;
import net.jxta.compatibility.platform.NetworkManager;
import net.jxta.platform.Module;

public class NetworkConfigurationFactory extends AbstractDependencyFactory<Module, IJp2pComponent<NetworkManager>> {

	private Collection<SeedListPropertySource> seedlists;
	
	public NetworkConfigurationFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parentSource) {
		super( container, parentSource );
	}

	@Override
	public String getComponentName() {
		return JxtaCompatComponents.NETWORK_CONFIGURATOR.toString();
	}
	
	@Override
	protected NetworkConfigurationPropertySource onCreatePropertySource() {
		NetworkConfigurationPropertySource source = new NetworkConfigurationPropertySource( (NetworkManagerPropertySource) super.getParentSource() );
		seedlists = new ArrayList<SeedListPropertySource>();
		SeedListPropertySource slps = new SeedListPropertySource( source, source.getClass() );
		if( slps.hasSeeds() )
			seedlists.add(slps);
		return source;
	}
	
	@Override
	protected boolean isCorrectFactory(IComponentFactory<?> factory) {
		if(!( factory.getComponent() instanceof IJp2pComponent ))
			return false;
		IJp2pComponent<?> component = (IJp2pComponent<?>) factory.getComponent();
		return ( component.getModule() instanceof NetworkManager);
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		String name = StringStyler.styleToEnum( event.getFactory().getComponentName() );
		if( !JxtaComponents.isComponent(name ))
			return;
		switch( event.getBuilderEvent() ){
		case PROPERTY_SOURCE_CREATED:
			if( !isComponentFactory( JxtaCompatComponents.SEED_LIST, event.getFactory() ))
				return;
			if( !NetworkConfigurationPropertySource.isChild(super.getPropertySource(), event.getFactory().getPropertySource()))
				return;
			seedlists.add( (SeedListPropertySource) event.getFactory().getPropertySource() );
			break;
		default:
			break;
		}
		super.notifyChange(event);
	}

	@Override
	protected IJp2pComponent<Module> onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		NetworkConfigurator configurator = null;
		try {
			NetworkManager manager = super.getDependency().getModule();
			configurator = manager.getConfigurator();
			URI home = (URI) super.getPropertySource().getProperty( NetworkConfiguratorProperties.HOME );
			if( home != null )
				configurator.setHome( new File( home ));
			this.fillConfigurator(configurator);
			configurator.clearRelaySeeds();
			configurator.clearRendezvousSeeds();
			for( SeedListPropertySource source: this.seedlists )
				SeedListFactory.fillSeeds(configurator, source);
			configurator.save();
		} catch (IOException e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
			return null;
		}
		return new NetworkConfiguratorService( (NetworkConfigurationPropertySource) super.getPropertySource(), configurator );
	}
	
	protected void fillConfigurator( NetworkConfigurator configurator ) throws IOException{
		this.fillPartialConfigurator( configurator, super.getPropertySource());
	}

	@SuppressWarnings({ "unchecked" })
	private void fillPartialConfigurator( NetworkConfigurator configurator, IJp2pPropertySource<?> source ) throws IOException{
		INetworkPreferences preferences;
		if( source instanceof SeedListPropertySource )
			return;
		
		if( source instanceof PartialPropertySource ){
			preferences = getPreferences(( PartialPropertySource )source);
			preferences.fillConfigurator( configurator );
			return;
		}
		preferences = new OverviewPreferences((IJp2pWritePropertySource<IJp2pProperties>) source );
		preferences.fillConfigurator(configurator);
		for( IJp2pPropertySource<?> child: super.getPropertySource().getChildren() )
			this.fillPartialConfigurator( configurator, (IJp2pPropertySource<NetworkConfiguratorProperties>) child);
	}

	/**
	 * Get the correct preferences for several services
	 * @param source
	 * @return
	 */
	public static INetworkPreferences getPreferences( PartialPropertySource source ){
		JxtaComponents component = JxtaComponents.valueOf( StringStyler.styleToEnum( source.getComponentName()));
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