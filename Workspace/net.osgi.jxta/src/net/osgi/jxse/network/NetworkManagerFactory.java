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
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.JxseComponentNode;
import net.osgi.jxse.context.ContextFactory;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class NetworkManagerFactory extends AbstractComponentFactory<NetworkManager>{
		
	public static final String S_WRN_NO_CONFIGURATOR = "Could not add network configurator";
	
	public NetworkManagerFactory( BuilderContainer container, IJxsePropertySource<IJxseProperties> parentSource ) {
		super( container, parentSource );
	}

	@Override
	public String getComponentName() {
		return Components.NETWORK_MANAGER.toString();
	}
	
	@Override
	protected NetworkManagerPropertySource onCreatePropertySource() {
		NetworkManagerPropertySource source = new NetworkManagerPropertySource( (JxseContextPropertySource) super.getParentSource());
		NetworkManagerPropertySource.setParentDirective(Directives.AUTO_START, source);
		return source;
	}

	
	@Override
	public void extendContainer() {
		BuilderContainer container = super.getContainer();
		IComponentFactory<?> ncf = container.getFactory( Components.NETWORK_CONFIGURATOR.toString() );
		if( ncf == null )
			ncf = container.addFactoryToContainer( Components.NETWORK_CONFIGURATOR.toString(), this, true, true );
		super.extendContainer();
	}

	@Override
	protected void onParseDirectivePriorToCreation( IJxseDirectives directive, Object value) {
		switch(( IJxseDirectives.Directives )directive ){
		case CLEAR_CONFIG:
			Path path = Paths.get(( URI )super.getPropertySource().getProperty( NetworkManagerProperties.INSTANCE_HOME ));
			if(Files.exists(path, LinkOption.NOFOLLOW_LINKS )){
				File file = path.toFile();
				NetworkManager.RecursiveDelete( file );
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected IJxseComponent<NetworkManager> onCreateComponent( IJxsePropertySource<IJxseProperties> properties) {
		// Removing any existing configuration?
		NetworkManagerPreferences preferences = new NetworkManagerPreferences( (IJxseWritePropertySource<IJxseProperties>) properties );
		String name = preferences.getInstanceName();
		try {
			Path path = Paths.get( preferences.getHomeFolder() );
			if(!Files.exists(path, LinkOption.NOFOLLOW_LINKS )){
				try {
					Files.createDirectories( path );
				} catch (IOException e1) {
					e1.printStackTrace();
					return null;
				}
			}
			File file = path.toFile();
			NetworkManager manager = new NetworkManager( preferences.getConfigMode(), name, file.toURI());
			return new JxseComponentNode<NetworkManager>( manager );
		} catch (Exception e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		switch( event.getBuilderEvent()){
		case COMPONENT_CREATED:
			if( !isComponentFactory( Components.JXSE_CONTEXT, event.getFactory() ))
				break;
			IComponentFactory<?> factory = event.getFactory();
			ContextFactory cf = (ContextFactory) factory;
			if( cf.isAutoStart() )
				this.createComponent();
			break;
		default:
			break;
		}
		super.notifyChange(event);
	}	
}