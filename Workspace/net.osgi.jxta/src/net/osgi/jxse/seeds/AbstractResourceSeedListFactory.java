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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jxse.seeds.SeedInfo;
import net.osgi.jxse.utils.IOUtils;

public abstract class AbstractResourceSeedListFactory implements ISeedListFactory{

	protected static final String S_SEEDS = "/JXTA-INF/seeds.txt";
	
	protected abstract InputStream getInputStream();
	
	private NetworkConfigurator configurator;
	
	public AbstractResourceSeedListFactory( NetworkConfigurator configurator ) {
		super();
		this.configurator = configurator;
	}


	/* (non-Javadoc)
	 * @see net.osgi.jxta.ISeedListFactory#createSeedlist(net.jxta.platform.NetworkConfigurator)
	 */
	@Override
	public String createModule(){
		configurator.clearRendezvousSeeds();
		InputStream in = this.getInputStream();
		Properties props = new Properties();
		SeedInfo seedInfo = new SeedInfo();
		try {
			props.load( in );
			Enumeration<?> enm = props.keys();
			String key, value;
			while( enm.hasMoreElements() ){
				key = ( String )enm.nextElement();
				value = (String) props.get(key);
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
		} catch (IOException e) {
			throw new RuntimeException( e );
		}
		finally{
			IOUtils.closeInputStream( in );
		}
		return null;
	}
}