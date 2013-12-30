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
package net.osgi.jxse.startup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import net.osgi.jxse.activator.AbstractActivator;
import net.osgi.jxse.activator.IActivator;
import net.osgi.jxse.activator.IJxseService;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.utils.Utils;

public class JxseStartupService extends AbstractActivator implements IJxseService<BuilderContainer>{

	public static final String S_ERR_NO_SERVICE_LOADED = "\n\t!!! No service is loaded. Not starting context:  ";
	public static final String S_ERR_CONTEXT_NOT_BUILT = "\n\t!!! The context was not built! Not starting context:  ";
	public static final String S_INFO_AUTOSTART = "\n\t!!! Autostarting container:  ";

	private JxseStartupPropertySource source;
	
	private BuilderContainer container;
	
	private Collection<ICompositeBuilderListener<Object>> listeners;
	
	public JxseStartupService( BuilderContainer container, JxseStartupPropertySource source ) {
		this.source = source;
		this.container = container;
		listeners = new ArrayList<ICompositeBuilderListener<Object>>();
		super.setStatus(Status.AVAILABLE);
	}

	/**
	 * If true, the service is auto started
	 * @return
	 */
	protected boolean isAutoStart(){
		return Boolean.parseBoolean( (String) this.source.getDirective( IJxseDirectives.Directives.AUTO_START ));		
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
		for( IComponentFactory<?> factory: container.getChildren()){
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
		return this.source.getProperty((IJxseProperties) key);
	}


	@Override
	public BuilderContainer getModule() {
		return this.container;
	}


	@Override
	public Iterator<IJxseProperties> iterator() {
		return this.source.propertyIterator();
	}

	@Override
	public String getCategory(Object key) {
		return this.source.getCategory( (IJxseProperties) key);
	}
	
}