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
package net.osgi.jp2p.startup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import net.osgi.jp2p.activator.AbstractActivator;
import net.osgi.jp2p.activator.IActivator;
import net.osgi.jp2p.activator.IJp2pService;
import net.osgi.jp2p.builder.ICompositeBuilderListener;
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.utils.Utils;

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
	protected void stopModule( IComponentFactory<Object> factory ){
		if( factory.getComponent() instanceof IActivator ){
			IActivator service = (IActivator) factory.getComponent();
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
		for( IComponentFactory<?> factory: container.getFactories()){
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

	@Override
	public Date getCreateDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getProperty(Object key) {
		return this.source.getProperty((IJp2pProperties) key);
	}


	@Override
	public IContainerBuilder getModule() {
		return this.container;
	}


	@Override
	public Iterator<IJp2pProperties> iterator() {
		return this.source.propertyIterator();
	}

	@Override
	public String getCategory(Object key) {
		return this.source.getCategory( (IJp2pProperties) key);
	}
	
}