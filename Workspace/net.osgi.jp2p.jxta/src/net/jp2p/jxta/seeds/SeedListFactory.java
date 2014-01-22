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
package net.jp2p.jxta.seeds;

import java.util.Iterator;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.factory.AbstractComponentFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IJp2pDirectives.Directives;
import net.jp2p.container.seeds.SeedInfo;
import net.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.jxta.platform.NetworkConfigurator;

public class SeedListFactory extends AbstractComponentFactory<String> implements ISeedListFactory{

	private NetworkConfigurator configurator;
	
	public SeedListFactory( IContainerBuilder container,  IJp2pPropertySource<IJp2pProperties> parent ) {
		super( container, parent);
	}

	@Override
	public String getComponentName() {
		return JxtaComponents.SEED_LIST.toString();
	}
	
	@Override
	protected SeedListPropertySource onCreatePropertySource() {
		return new SeedListPropertySource( super.getParentSource() );
	}

	public void addSeed( IJp2pProperties name, String value ){
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) super.getPropertySource();
		source.setProperty( name, value);
	}
	
	@Override
	public void setConfigurator(NetworkConfigurator configurator) {
		this.configurator = configurator;
		super.setCanCreate(this.configurator != null );
	}

	@Override
	public IJp2pComponent<String> createComponent() {
		return super.createComponent();
	}

	@Override
	protected IJp2pComponent<String> onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) super.getPropertySource();
		Iterator<IJp2pProperties> iterator = source.propertyIterator();
		while( iterator.hasNext() ){
			IJp2pProperties key = iterator.next();
			if(!( source.getProperty(key) instanceof SeedInfo ))
				continue;
			SeedInfo seedInfo = ( SeedInfo ) source.getProperty( key);
			switch( seedInfo.getSeedType() ){
			case RDV:
				configurator.addSeedRendezvous( seedInfo.getUri() );
				break;
			case RELAY:
				configurator.addRelaySeedingURI( seedInfo.getUri() );						
			}
		}
		return null;
	}

	@Override
	public boolean isCompleted() {
		if( super.getPropertySource() == null )
			return false;
		SeedListPropertySource source = (SeedListPropertySource) super.getPropertySource();
		return SeedListPropertySource.getBoolean(source, Directives.BLOCK_CREATION);
	}
}