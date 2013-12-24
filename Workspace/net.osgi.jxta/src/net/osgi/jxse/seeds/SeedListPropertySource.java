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

import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.IOUtils;
import net.osgi.jxse.utils.StringProperty;

public class SeedListPropertySource extends AbstractJxseWritePropertySource {

	public static final String S_SEED_LIST = "Seed List";

	protected static final String S_SEEDS = "/JXSE-INF/seeds.txt";

	private boolean hasSeeds;
	
	public SeedListPropertySource( IJxsePropertySource<IJxseProperties> parent ) {
		super( S_SEED_LIST, parent );
	}

	public SeedListPropertySource( IJxsePropertySource<IJxseProperties> parent, Class<?> clss ) {
		this( parent );
		this.hasSeeds = false;
		this.fillProperties( clss );
	}
	
	protected void fillProperties( Class<?> clss ){
		InputStream in = clss.getResourceAsStream( S_SEEDS );
		if( in == null )
			return;
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
				this.setProperty( new StringProperty( key ), seedInfo );
				
			}
		} catch (IOException e) {
			throw new RuntimeException( e );
		}
		finally{
			IOUtils.closeInputStream( in );
		}
	}

	
	public boolean setProperty(IJxseProperties id, SeedInfo value ) {
		boolean retval = super.setProperty(id, value);
		this.hasSeeds |= retval;
		return retval;
	}

	/**
	 * Returns true if this seedlist
	 * @return
	 */
	public boolean hasSeeds() {
		return hasSeeds;
	}

	@Override
	public IJxseProperties getIdFromString(String key) {
		return new StringProperty( key );
	}

	@Override
	public boolean validate(IJxseProperties id, Object value) {
		return false;
	}
}