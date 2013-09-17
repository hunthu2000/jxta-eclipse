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
package net.osgi.jxse.preferences;

import java.util.Iterator;

import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.ManagedProperty;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.service.prefs.Preferences;

public class AbstractJxsePreferences< T extends Enum<T>, U extends IJxseDirectives> implements IJxsePropertySource<T, U> 
{
	private Preferences preferences;
	private Preferences jxtaPreferences;
	private IJxsePropertySource<T, U> source;
		
	public AbstractJxsePreferences( IJxsePropertySource<T, U> source )
	{
		this.source = source;
		preferences = ConfigurationScope.INSTANCE.getNode( source.getBundleId() );
		jxtaPreferences = preferences.node(IJxsePropertySource.JXTA_SETTINGS);
		jxtaPreferences.put( ContextProperties.IDENTIFIER.toString(), source.getIdentifier() );
	}
		
	/**
	 * Set the preference for the given id and add a string representation of the value
	 * @param id
	 * @return
	 */
	protected void put( T id, Object value, String representation ){
		preferences.put( id.name(), representation );
		source.setProperty(id, value);
	}

	/**
	 * Get the config modes as String
	 * @return
	 */
	public static String[] getConfigModes(){
		ConfigMode[] modes = ConfigMode.values();
		String[] results = new String[ modes.length ];
		for( int i=0; i<modes.length; i++ )
			results[i] = modes[i].toString();
		return results;	
	}

	@Override
	public String getBundleId() {
		return this.source.getBundleId();
	}

	@Override
	public String getIdentifier() {
		return this.source.getIdentifier();
	}

	@Override
	public String getComponentName() {
		return this.source.getComponentName();
	}

	@Override
	public String getId() {
		return this.source.getId();
	}

	@Override
	public int getDepth() {
		return this.source.getDepth();
	}

	@Override
	public Object getDefault(T id) {
		return this.source.getDefault(id);
	}

	@Override
	public Object getProperty(T id) {
		return preferences.get( id.name(), (String) this.source.getDefault( id ));
	}

	@Override
	public boolean setProperty(T id, Object value) {
		if(( id == null ) || ( value == null ))
			return false;
		this.put( id, value, value.toString() );
		return true;
	}

	@Override
	public Iterator<T> propertyIterator() {
		return this.source.propertyIterator();
	}

	@Override
	public boolean validate(T id, Object value) {
		return this.source.validate(id, value);
	}

	@Override
	public Object getDirective(U id) {
		return this.source.getDirective(id);
	}

	@Override
	public Object getDefaultDirectives(U id) {
		return this.source.getDefaultDirectives(id);
	}

	@Override
	public Iterator<U> directiveIterator() {
		return this.source.directiveIterator();
	}

	@Override
	public IJxsePropertySource<?, ?>[] getChildren() {
		return this.source.getChildren();
	}

	@Override
	public boolean setDirective(U id, Object value) {
		return this.source.setDirective(id, value);
	}

	@Override
	public ManagedProperty<T, Object> getManagedProperty(T id) {
		return this.source.getManagedProperty(id);
	}
}
