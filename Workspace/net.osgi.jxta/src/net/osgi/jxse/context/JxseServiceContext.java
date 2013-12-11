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
package net.osgi.jxse.context;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.context.AbstractServiceContext;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class JxseServiceContext extends AbstractServiceContext<IJxseProperties,  IJxseDirectives>{

	public JxseServiceContext( IJxsePropertySource<IJxseProperties, IJxseDirectives> iJxsePropertySource ) {
		super( iJxsePropertySource );
	}

	@Override
	public void clearModules(){
		super.getChildren().clear();
	}

	@Override
	public NetworkManager getModule() {
		for( IJxseComponent<?,?> component: super.getChildren() ){
			if( component.getModule() instanceof NetworkManager ){
				return ( NetworkManager )component.getModule();
			}
		}
		return super.getModule();
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
		for( IJxseComponent<?,?> component: super.getChildren() ){
			if( component.getModule() instanceof NetworkManager ){
				NetworkManager manager = ( NetworkManager )component.getModule();
				manager.stopNetwork();
				break;
			}
		}
		super.deactivate();
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