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
package net.jp2p.container;

import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.startup.Jp2pStartupService;

public class Jp2pServiceContainer extends AbstractServiceContainer<Jp2pStartupService>{

	public Jp2pServiceContainer( IJp2pPropertySource<IJp2pProperties> iJxsePropertySource ) {
		super( iJxsePropertySource );
	}

	@Override
	public void clearModules(){
		super.getChildren().clear();
	}

	@Override
	public Jp2pStartupService getModule() {
		for( IJp2pComponent<?> component: super.getChildren() ){
			if( component.getModule() instanceof Jp2pStartupService ){
				return ( Jp2pStartupService )component.getModule();
			}
		}
		return super.getModule();
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
	
	@Override
	protected void onFinalising() {
		// DO NOTHING AS DEFAULT ACTION		
	}

	@Override
	protected boolean onInitialising() {
		return true;
	}
	
}