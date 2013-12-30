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
package net.osgi.jxse.seeds;

import java.util.Iterator;

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.seeds.SeedInfo;

public class SeedListFactory extends AbstractComponentFactory<String> implements ISeedListFactory{

	private NetworkConfigurator configurator;
	
	public SeedListFactory( BuilderContainer container,  IJxsePropertySource<IJxseProperties> parent ) {
		super( container, parent);
	}

	@Override
	public String getComponentName() {
		return Components.SEED_LIST.toString();
	}
	
	@Override
	protected SeedListPropertySource onCreatePropertySource() {
		return new SeedListPropertySource( super.getParentSource() );
	}

	public void addSeed( IJxseProperties name, String value ){
		IJxseWritePropertySource<IJxseProperties> source = (IJxseWritePropertySource<IJxseProperties>) super.getPropertySource();
		source.setProperty( name, value);
	}
	
	@Override
	public void setConfigurator(NetworkConfigurator configurator) {
		this.configurator = configurator;
		super.setCanCreate(this.configurator != null );
	}

	@Override
	public IJxseComponent<String> createComponent() {
		return super.createComponent();
	}

	@Override
	protected IJxseComponent<String> onCreateComponent( IJxsePropertySource<IJxseProperties> properties) {
		IJxseWritePropertySource<IJxseProperties> source = (IJxseWritePropertySource<IJxseProperties>) super.getPropertySource();
		Iterator<IJxseProperties> iterator = source.propertyIterator();
		while( iterator.hasNext() ){
			IJxseProperties key = iterator.next();
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