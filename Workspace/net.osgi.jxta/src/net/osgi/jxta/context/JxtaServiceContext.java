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
package net.osgi.jxta.context;

import java.util.HashMap;
import java.util.Map;

import net.jxta.platform.NetworkManager;
import net.osgi.jxta.component.IJxtaComponent;
import net.osgi.jxta.context.AbstractServiceContext;

public class JxtaServiceContext extends AbstractServiceContext<NetworkManager>{

	private NetworkManager manager;
	
	private Map<Object,Object> properties;
	
	public JxtaServiceContext( String identifier ) {
		super( identifier );
		properties = new HashMap<Object, Object>();
	}

	@Override
	public void clearModules(){
		super.getChildren().clear();
	}
	
	/**
	 * Add a module to the container
	 * @param module
	 */
	protected void addModule( Object module ){
		addModule( this, module );
	}

	protected void removeModule( Object module ){
		removeModule( this, module );
	}

	/**
	 * Make public
	 */
	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	protected void activate() {
		super.activate();
	}
	
	//Make public
	@Override
	public void deactivate() {
		for( IJxtaComponent<?> component: super.getChildren() ){
			if( component.getModule() instanceof NetworkManager ){
				NetworkManager manager = ( NetworkManager )component.getModule();
				manager.stopNetwork();
				break;
			}
		}
		super.deactivate();
	}
	
	@Override
	public Object getProperty(Object key) {
		return properties.get(key);
	}

	@Override
	public void putProperty(Object key, Object value) {
		properties.put(key, value);
	}

	@Override
	public NetworkManager getModule() {
		return manager;
	}

	@Override
	protected void onFinalising() {
		// DO NOTHING AS DEFAULT ACTION		
	}

	@Override
	protected boolean onInitialising() {
		return true;
	}
	
}