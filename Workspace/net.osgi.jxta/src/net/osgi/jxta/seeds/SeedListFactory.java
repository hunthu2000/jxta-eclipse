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
package net.osgi.jxta.seeds;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jxta.seeds.SeedInfo;

public class SeedListFactory implements ISeedListFactory{

	private Map<String, String> props;
	
	
	public SeedListFactory() {
		super();
		props = new HashMap<String, String>();
	}


	public void addSeed( String name, String value ){
		props.put( name, value);
	}
	
	/* (non-Javadoc)
	 * @see net.osgi.jxta.ISeedListFactory#createSeedlist(net.jxta.platform.NetworkConfigurator)
	 */
	@Override
	public void createSeedlist( NetworkConfigurator configurator ) throws IOException{
		configurator.clearRendezvousSeeds();
		SeedInfo seedInfo = new SeedInfo();
		String value;
		for( String key: props.keySet() ){
			value = props.get(key);
			seedInfo.parse(key, value );
			if( seedInfo.isCommentedOut() )
				continue;
			switch( seedInfo.getSeedType() ){
			case RDV:
				configurator.addSeedRendezvous( seedInfo.getUri() );
				break;
			case RELAY:
				configurator.addRelaySeedingURI( seedInfo.getUri() );						
			}
		}
	}


	@Override
	public boolean isEmpty() {
		return props.isEmpty();
	}		
}