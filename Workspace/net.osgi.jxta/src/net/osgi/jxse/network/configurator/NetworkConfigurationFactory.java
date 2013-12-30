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
package net.osgi.jxse.network.configurator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.JxseComponent;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.network.INetworkPreferences;
import net.osgi.jxse.network.NetworkManagerPropertySource;
import net.osgi.jxse.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jxse.network.http.Http2Preferences;
import net.osgi.jxse.network.http.HttpPreferences;
import net.osgi.jxse.network.multicast.MulticastPreferences;
import net.osgi.jxse.network.security.SecurityPreferences;
import net.osgi.jxse.network.tcp.TcpPreferences;
import net.osgi.jxse.partial.PartialPropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.seeds.SeedListFactory;
import net.osgi.jxse.seeds.SeedListPropertySource;
import net.osgi.jxse.utils.StringStyler;

public class NetworkConfigurationFactory extends
		AbstractComponentFactory<NetworkConfigurator> {

	private NetworkManager manager;

	public NetworkConfigurationFactory( BuilderContainer container, IJxsePropertySource<IJxseProperties> parentSource) {
		super( container, parentSource, false );
		super.setCanCreate(this.manager != null );
	}

	@Override
	public String getComponentName() {
		return Components.NETWORK_CONFIGURATOR.toString();
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
	protected IJxseComponent<NetworkConfigurator> onCreateComponent( IJxsePropertySource<IJxseProperties> properties) {
		NetworkConfigurator configurator = null;
		try {
			configurator = manager.getConfigurator();
			URI home = (URI) super.getPropertySource().getProperty( NetworkConfiguratorProperties.HOME );
			if( home != null )
				configurator.setHome( new File( home ));
			this.fillConfigurator(configurator);
			configurator.clearRelaySeeds();
			configurator.clearRendezvousSeeds();
			SeedListFactory slf = (SeedListFactory) super.getContainer().getFactory( Components.SEED_LIST.toString() );
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
		return new JxseComponent<NetworkConfigurator>( configurator );
	}
	
	protected void fillConfigurator( NetworkConfigurator configurator ) throws IOException{
		this.fillPartialConfigurator( configurator, super.getPropertySource());
	}

	@SuppressWarnings({ "unchecked" })
	private void fillPartialConfigurator( NetworkConfigurator configurator, IJxsePropertySource<?> source ) throws IOException{
		INetworkPreferences preferences;
		if( source instanceof SeedListPropertySource )
			return;
		
		if( source instanceof PartialPropertySource ){
			preferences = getPreferences(( PartialPropertySource )source);
			preferences.fillConfigurator( configurator );
			return;
		}
		preferences = new OverviewPreferences((IJxseWritePropertySource<IJxseProperties>) source );
		preferences.fillConfigurator(configurator);
		for( IJxsePropertySource<?> child: super.getPropertySource().getChildren() )
			this.fillPartialConfigurator( configurator, (IJxsePropertySource<NetworkConfiguratorProperties>) child);
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
			break;
		}	
	}
	
	@Override
	public boolean complete() {
		boolean retval = super.complete();
		for( IJxsePropertySource<?> source: getPropertySource().getChildren() ){
			if( source instanceof SeedListPropertySource ){
				SeedListFactory slf = new SeedListFactory( super.getContainer(), (SeedListPropertySource) source );
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
		if( !Components.isComponent(name ))
			return;
		Components component = Components.valueOf(name);
		switch( event.getBuilderEvent() ){
		case PROPERTY_SOURCE_CREATED:
			switch( component){
			case SEED_LIST:
				SeedListPropertySource source = (SeedListPropertySource) event.getFactory().getPropertySource();
				source.setDirective( Directives.BLOCK_CREATION, Boolean.TRUE.toString());
				break;
			case TCP:
			case HTTP:
			case HTTP2:
			case SECURITY:
			case MULTICAST:
				break;
			default:
				break;
			}
			break;
		case COMPONENT_CREATED:
			if( !isComponentFactory( Components.NETWORK_MANAGER, event.getFactory() ))
				return;
			this.manager = ((IJxseComponent<NetworkManager>) event.getFactory().getComponent()).getModule();
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