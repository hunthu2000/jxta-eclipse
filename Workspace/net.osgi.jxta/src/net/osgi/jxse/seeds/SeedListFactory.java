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
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.seeds.SeedInfo;

public class SeedListFactory implements IComponentFactory<String, IJxseProperties>, ISeedListFactory{

	private SeedListPropertySource source;
	private NetworkConfigurator configurator;
	private boolean completed = false;
	
	public SeedListFactory( SeedListPropertySource source ) {
		super();
		this.source = source;
	}

	public void addSeed( IJxseProperties name, String value ){
		source.setProperty( name, value);
	}
	
	@Override
	public Components getComponent() {
		return Components.SEED_LIST;
	}

	@Override
	public boolean canCreate() {
		return true;
	}

	@Override
	public IJxsePropertySource<IJxseProperties> getPropertySource() {
		return source;
	}
	
	@Override
	public void setConfigurator(NetworkConfigurator configurator) {
		this.configurator = configurator;		
	}

	@Override
	public String createModule() {
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
	public boolean complete() {
		this.completed = true;
		return this.completed;
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public boolean hasFailed() {
		return !this.completed;
	}

	@Override
	public String getModule() {
		return null;
	}

	@Override
	public boolean moduleActive() {
		return true;
	}		
}