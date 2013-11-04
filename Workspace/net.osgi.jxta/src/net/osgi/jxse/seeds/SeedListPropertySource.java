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
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.IOUtils;

public class SeedListPropertySource<T extends IJxseDirectives> extends AbstractJxseWritePropertySource<String> {

	public static final String S_SEED_LIST = "Seed List";

	protected static final String S_SEEDS = "/JXSE-INF/seeds.txt";

	private boolean hasSeeds;
	
	public SeedListPropertySource( IJxsePropertySource<?, IJxseDirectives> parent ) {
		super( S_SEED_LIST, parent );
	}

	public SeedListPropertySource( IJxsePropertySource<?, IJxseDirectives> parent, Class<?> clss ) {
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
				this.setProperty(key, seedInfo );
				
			}
		} catch (IOException e) {
			throw new RuntimeException( e );
		}
		finally{
			IOUtils.closeInputStream( in );
		}
	}

	
	public boolean setProperty(String id, SeedInfo value ) {
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
	public String getIdFromString(String key) {
		return key;
	}

	@Override
	public boolean validate(String id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getDefaultDirectives(IJxseDirectives id) {
		// TODO Auto-generated method stub
		return null;
	}
}