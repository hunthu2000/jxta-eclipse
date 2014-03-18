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
package net.jp2p.container.startup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import net.jp2p.container.activator.AbstractActivator;
import net.jp2p.container.activator.IActivator;
import net.jp2p.container.activator.IJp2pService;
import net.jp2p.container.builder.ICompositeBuilderListener;
import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.factory.IComponentFactory;
import net.jp2p.container.factory.IPropertySourceFactory;
import net.jp2p.container.properties.IJp2pDirectives;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.utils.Utils;

public class Jp2pStartupService extends AbstractActivator implements IJp2pService<IContainerBuilder>{

	public static final String S_ERR_NO_SERVICE_LOADED = "\n\t!!! No service is loaded. Not starting context:  ";
	public static final String S_ERR_CONTEXT_NOT_BUILT = "\n\t!!! The context was not built! Not starting context:  ";
	public static final String S_INFO_AUTOSTART = "\n\t!!! Autostarting container:  ";

	private Jp2pStartupPropertySource source;
	
	private IContainerBuilder container;
	
	private Collection<ICompositeBuilderListener<Object>> listeners;
	
	public Jp2pStartupService( IContainerBuilder container, Jp2pStartupPropertySource source ) {
		this.source = source;
		this.container = container;
		listeners = new ArrayList<ICompositeBuilderListener<Object>>();
		super.setStatus(Status.AVAILABLE);
	}

	@Override
	public IJp2pPropertySource<IJp2pProperties> getPropertySource() {
		return this.source;
	}


	/**
	 * If true, the service is auto started
	 * @return
	 */
	protected boolean isAutoStart(){
		return Boolean.parseBoolean( (String) this.source.getDirective( IJp2pDirectives.Directives.AUTO_START ));		
	}

	/**
	 * Start a module
	 * @param module
	 */
	protected void stopModule( IPropertySourceFactory factory ){
		if(!( factory instanceof IComponentFactory<?> ))
			return;
		if( ((IComponentFactory<?>) factory).getComponent() instanceof IActivator ){
			IActivator service = (IActivator) ((IComponentFactory<?>) factory).getComponent();
			if( service.isActive())
				service.stop();
		}		
	}
	
	/**
	 * Perform the activation
	 */
	public synchronized void activate() {
		if(!this.isAutoStart() )
			return;
		
		//listeners.add(this.container);
		
		//First start the modules that are already present
		//for( IJxseModule<?> module: container.getChildren()){
		//	if( Components.STARTUP_SERVICE.toString().equals( module.getComponentName() ))
		//		continue;
		//	try{
		//	  this.startModule( (IJxseModule<Object>) module);
		//	}
		//	catch( Exception ex ){
		//		ex.printStackTrace();
		//	}
		//}
		
		//Then listen to new additions
		Logger logger = Logger.getLogger( this.getClass().getName());
		String list = container.listModulesNotCompleted();
		if( !Utils.isNull( list )){
			logger.warning( list );
		}
	}
	
	//Make public
	@SuppressWarnings("unchecked")
	@Override
	public void deactivate() {
		this.listeners.remove(this.container);
		for( IPropertySourceFactory factory: container.getFactories()){
			this.stopModule( (IComponentFactory<Object>) factory );
		}
	}

	@Override
	protected void onFinalising() {
		// DO NOTHING AS DEFAULT ACTION		
	}

	@Override
	protected boolean onInitialising() {
		return true;
	}


	@Override
	public String getId() {
		return this.source.getId();
	}

	/**
	 * Get a String label for this component. This can be used for display options and 
	 * is not meant to identify the component;
	 * @return
	 */
	public String getComponentLabel(){
		return this.source.getComponentName();
	}

	@Override
	public IContainerBuilder getModule() {
		return this.container;
	}
}