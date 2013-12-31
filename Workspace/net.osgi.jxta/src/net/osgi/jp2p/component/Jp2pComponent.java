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
package net.osgi.jp2p.component;

import java.util.Date;
import java.util.Iterator;

import net.osgi.jp2p.properties.AbstractJp2pPropertySource;
import net.osgi.jp2p.properties.DefaultPropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;
import net.osgi.jp2p.utils.StringProperty;

public class Jp2pComponent<T extends Object> implements IJp2pComponent<T>, Comparable< IJp2pComponent<?>>{

	public static final String S_DEFAULT_PROPERTY = "Default";
	private T module;
	private IJp2pWritePropertySource<IJp2pProperties> source;
	private IJp2pComponent<?> parent;

	public Jp2pComponent( T component ) {
		this( null, component );
	}

	public Jp2pComponent( IJp2pComponent<?> parent, T component ) {
		this.module = component;
		this.parent = parent;
		String id = this.getClass().getName();
		if( parent != null )
			id = parent.getId();
		this.source = new DefaultPropertySource( id, S_DEFAULT_PROPERTY );
	}

	@Override
	public String getId() {
		return ( String )this.source.getDirective( Directives.ID );
	}

	/**
	 * Get the create date
	 */
	@Override
	public Date getCreateDate(){
		return (Date) this.source.getProperty(ModuleProperties.CREATE_DATE);
	}

	/**
	 * Return true if the component is a root
	 * @return
	 */
	public boolean isRoot(){
		return ( this.parent == null );
	}
	/**
	 * Get the parent of the component
	 * @return
	 */
	public IJp2pComponent<?> getParent(){
		return parent;
	}

	@Override
	public T getModule() {
		return this.module;
	}

	@Override
	public Object getProperty(Object key) {
		return AbstractJp2pPropertySource.getExtendedProperty(source, (IJp2pProperties) key);
	}

	/**
	 * Get the category for the given key
	 * @param key
	 * @return
	 */
	public String getCategory( Object key ){
		return ManagedProperty.S_DEFAULT_CATEGORY;
	}

	protected void putProperty(IJp2pProperties key, Object value ) {
		String[] split = key.toString().split("[.]");
		StringProperty id = new StringProperty( split[ split.length - 1]);
		ManagedProperty<IJp2pProperties, Object> mp = source.getOrCreateManagedProperty(id, value, false);
		mp.setValue(value);
	}

	@Override
	public int compareTo(IJp2pComponent<?> o) {
		return Integer.MAX_VALUE;
	}

	@Override
	public Iterator<IJp2pProperties> iterator() {
		return AbstractJp2pPropertySource.getExtendedIterator(source);
	}
}
