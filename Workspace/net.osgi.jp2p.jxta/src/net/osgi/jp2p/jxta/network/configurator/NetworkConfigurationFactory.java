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
package net.osgi.jp2p.jxta.network.configurator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.component.Jp2pComponent;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.osgi.jp2p.jxta.network.INetworkPreferences;
import net.osgi.jp2p.jxta.network.NetworkManagerPropertySource;
import net.osgi.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jp2p.jxta.network.http.Http2Preferences;
import net.osgi.jp2p.jxta.network.http.HttpPreferences;
import net.osgi.jp2p.jxta.network.multicast.MulticastPreferences;
import net.osgi.jp2p.jxta.network.security.SecurityPreferences;
import net.osgi.jp2p.jxta.network.tcp.TcpPreferences;
import net.osgi.jp2p.jxta.seeds.SeedListFactory;
import net.osgi.jp2p.jxta.seeds.SeedListPropertySource;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource;
import net.osgi.jp2p.jxta.network.configurator.OverviewPreferences;
import net.osgi.jp2p.partial.PartialPropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;

public class NetworkConfigurationFactory extends
		AbstractComponentFactory<NetworkConfigurator> {

	private NetworkManager manager;

	public NetworkConfigurationFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parentSource) {
		super( container, parentSource );
		super.setCanCreate(this.manager != null );
	}

	@Override
	public String getComponentName() {
		return JxtaComponents.NETWORK_CONFIGURATOR.toString();
	}
	
	@Override
	protected NetworkConfigurationPropertySource onCreatePropertySource() {
		NetworkConfigurationPropertySource source = new NetworkConfigurationPropertySource( (NetworkManagerPropertySource) super.getParentSource() );
		SeedListPropertySource slps = new SeedListPropertySource( source, source.getClass() );
		if( slps.hasSeeds() )
			source.addChild(slps);
		return source;
	}

	@Override
	protected IJp2pComponent<NetworkConfigurator> onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		NetworkConfigurator configurator = null;
		try {
			configurator = manager.getConfigurator();
			URI home = (URI) super.getPropertySource().getProperty( NetworkConfiguratorProperties.HOME );
			if( home != null )
				configurator.setHome( new File( home ));
			this.fillConfigurator(configurator);
			configurator.clearRelaySeeds();
			configurator.clearRendezvousSeeds();
			SeedListFactory slf = (SeedListFactory) super.getBuilder().getFactory( JxtaComponents.SEED_LIST.toString() );
			if( slf != null ){
				slf.createPropertySource();
				slf.setConfigurator(configurator);
				slf.createComponent();
			}
			configurator.save();
		} catch (IOException e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
			return null;
		}
		return new Jp2pComponent<NetworkConfigurator>( super.getPropertySource(), configurator );
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

	@Override
	protected void onParseDirectiveAfterCreation(IJp2pDirectives directive,
			Object value) {
		if(!( directive instanceof Directives ))
			return;
		Directives dir = ( Directives )directive;
		switch( dir )
		{
		default:
			break;
		}	
	}
	
	@Override
	public boolean complete() {
		boolean retval = super.complete();
		for( IJp2pPropertySource<?> source: getPropertySource().getChildren() ){
			if( source instanceof SeedListPropertySource ){
				SeedListFactory slf = new SeedListFactory( super.getBuilder(), (SeedListPropertySource) source );
				retval &= slf.complete();
			}
		}
		super.setCompleted(retval);
		return retval;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		String name = StringStyler.styleToEnum( event.getFactory().getComponentName() );
		if( !JxtaComponents.isComponent(name ))
			return;
		JxtaComponents component = JxtaComponents.valueOf(name);
		switch( event.getBuilderEvent() ){
		case PROPERTY_SOURCE_CREATED:
			switch( component){
			case SEED_LIST:
				SeedListPropertySource source = (SeedListPropertySource) event.getFactory().getPropertySource();
				source.setDirective( Directives.BLOCK_CREATION, Boolean.TRUE.toString());
				break;
			default:
				break;
			}
			break;
		case COMPONENT_CREATED:
			if( !isComponentFactory( JxtaComponents.NETWORK_MANAGER, event.getFactory() ))
				return;
			this.manager = ((IJp2pComponent<NetworkManager>) event.getFactory().getComponent()).getModule();
			super.setCanCreate(this.manager != null );
			createComponent();
			break;
		default:
			break;
		}
	}

	/**
	 * Get the correct preferences for several services
	 * @param source
	 * @return
	 */
	public static INetworkPreferences getPreferences( PartialPropertySource source ){
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