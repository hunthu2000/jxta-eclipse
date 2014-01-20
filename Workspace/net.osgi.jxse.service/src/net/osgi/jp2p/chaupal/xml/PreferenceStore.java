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
package net.osgi.jp2p.chaupal.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.osgi.jp2p.utils.StringStyler;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.service.prefs.Preferences;

public class PreferenceStore
{
	/**
	 * Persitence options
	 * @author Kees
	 *
	 */
	public enum Persistence{
		NULL,
		TRANSIENT,
		PARSED,
		GENERATED,
		PREFERENCES; //Preferences take over if they are found, otherwise a default values is parsed
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	/**
	 * Supported attributes during parsing
	 * @author Kees
	 *
	 */
	public enum SupportedAttributes{
		ID,
		NAME,
		PERSIST;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public static final String JXTA_SETTINGS = "jxta.settings";

	private Preferences jxtaPreferences;
	private Collection<PersistentAttribute> attributes;
	
	public PreferenceStore( String plugin_id )
	{
		attributes = new ArrayList<PersistentAttribute>();
		Preferences preferences = ConfigurationScope.INSTANCE.getNode( plugin_id );
		jxtaPreferences = preferences.node(JXTA_SETTINGS);
	}

	public String getValue( String key ){
		PersistentAttribute pa = this.getAttribute(key);
		if( pa == null )
			return null;
		
		if( !pa.isPersistent() )
			return pa.getValue();
		return this.jxtaPreferences.get( key , pa.getValue());
	}

	public void setValue( String key, String value ){
		PersistentAttribute pa = getAttribute( key );
		if( pa == null )
			this.addPersistentAttribute(key, value);
		switch( pa.getPersistence() ){
		case GENERATED:
		case PARSED:
		case PREFERENCES:
			this.jxtaPreferences.put(key, value);
			break;
		default:
			return;
		}	
		
	}
	
	public void addPersistentAttribute( Map<SupportedAttributes, String> attributes, String key, String value){
		this.attributes.add( new PersistentAttribute( attributes, key, value ));
	}

	public void addPersistentAttribute( String key, String value){
		this.attributes.add( new PersistentAttribute(key, value ));
	}

	/**
	 * Get the persistent attribute for the given key
	 * @param key
	 * @return
	 */
	public PersistentAttribute getAttribute( String key ){
		if(( key == null ) || ( key.length() == 0 ))
			return null;
		for( PersistentAttribute pa: this.attributes ){
			if( key.equals( pa.getKey()))
					return pa;
		}
		return null;
	}

	/**
	 * Get the persistent attribute for the given key
	 * @param key
	 * @return
	 */
	public Persistence getPersistent( String key ){
		if(( key == null ) || ( key.length() == 0 ))
			return Persistence.NULL;
		for( PersistentAttribute pa: this.attributes ){
			if( key.equals( pa.getKey()))
					return pa.getPersistence();
		}
		return Persistence.PARSED;
	}
	
	public boolean isEmpty(){
		return this.attributes.isEmpty();
	}

	public int size(){
		return this.attributes.size();
	}	
}