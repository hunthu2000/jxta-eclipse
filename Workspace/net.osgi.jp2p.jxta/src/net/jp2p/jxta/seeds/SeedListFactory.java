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

import net.jp2p.container.factory.AbstractPropertySourceFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.seeds.SeedInfo;
import net.jp2p.jxta.factory.IJxtaComponents.JxtaNetworkComponents;
import net.jxta.compatibility.platform.NetworkConfigurator;

public class SeedListFactory extends AbstractPropertySourceFactory{

	@Override
	public String getComponentName() {
		return JxtaNetworkComponents.SEED_LIST.toString();
	}
	
	@Override
	protected SeedListPropertySource onCreatePropertySource() {
		return new SeedListPropertySource( super.getParentSource() );
	}

	/**
	 * Fill the configurator with the seeds
	 * @param configurator
	 * @param source
	 */
	public static void fillSeeds( NetworkConfigurator configurator, SeedListPropertySource source) {
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
	}
}