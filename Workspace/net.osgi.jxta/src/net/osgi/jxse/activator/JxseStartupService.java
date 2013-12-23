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
package net.osgi.jxse.activator;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import net.osgi.jxse.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.builder.container.BuilderContainer;
import net.osgi.jxse.builder.container.BuilderContainerEvent;
import net.osgi.jxse.builder.container.IBuilderContainerListener;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;

public class JxseStartupService extends AbstractActivator implements IJxseService<BuilderContainer>{

	public static final String S_ERR_NO_SERVICE_LOADED = "\n\t!!! No service is loaded. Not starting context:  ";
	public static final String S_ERR_CONTEXT_NOT_BUILT = "\n\t!!! The context was not built! Not starting context:  ";
	public static final String S_INFO_AUTOSTART = "\n\t!!! Autostarting container:  ";
	
	private JxseStartupPropertySource source;
	
	private BuilderContainer container;
	
	private IBuilderContainerListener<Object> listener;
	
	public JxseStartupService( BuilderContainer container, JxseStartupPropertySource source ) {
		this.source = source;
		this.container = container;
		super.setStatus(Status.AVAILABLE);
	}

	/**
	 * If true, the service is autostarted
	 * @return
	 */
	protected boolean isAutoStart(){
		return Boolean.parseBoolean( (String) this.source.getDirective( IJxseDirectives.Directives.AUTO_START ));		
	}
		
	/**
	 * Start a module
	 * @param module
	 */
	protected void startModule( IJxseModule<Object> module ){
		IComponentFactory<?> factory = null;
		if(( module.canCreate()) && ( !module.isCompleted() )){
				factory = module.createFactory();
				container.notifyCreated( new ComponentBuilderEvent<Object>( this, module, BuilderEvents.FACTORY_CREATED ));
		}
		if( JxseStartupPropertySource.isAutoStart( module.getPropertySource()) ){
			factory.createComponent();
			container.notifyCreated( new ComponentBuilderEvent<Object>( this, module, BuilderEvents.COMPONENT_CREATED ));			
		}
		if( module.canActivate() ){
			IActivator service = (IActivator) module.getComponent();
			if( !service.isActive())
				service.start();
		}		
	}

	/**
	 * Start a module
	 * @param module
	 */
	protected void stopModule( IJxseModule<Object> module ){
		if( module.canActivate() ){
			IActivator service = (IActivator) module.getComponent();
			if( service.isActive())
				service.stop();
		}		
	}
	

	/**
	 * Perform the activation
	 */
	@SuppressWarnings("unchecked")
	public synchronized void activate() {
		if(!this.isAutoStart() )
			return;
		
		Logger logger = Logger.getLogger( this.getClass().getName() );
		
		//First start the modules that are already present
		for( IJxseModule<?> module: container.getChildren()){
			if( Components.STARTUP_SERVICE.toString().equals( module.getComponentName() ))
				continue;
			this.startModule( (IJxseModule<Object>) module);
		}
		
		//Then listen to new additions
		listener = new IBuilderContainerListener<Object>(){

			@Override
			public void notifyAdded(BuilderContainerEvent<Object> event) {
				startModule( event.getModule() );
			}

			@Override
			public void notifyRemoved(BuilderContainerEvent<Object> event) {
				stopModule( event.getModule() );
			}
			
		};
		container.addListener( listener);
	}
	
	//Make public
	@SuppressWarnings("unchecked")
	@Override
	public void deactivate() {
		if( this.listener != null )
			container.removeListener(listener);
		for( IJxseModule<?> module: container.getChildren()){
			this.stopModule((IJxseModule<Object>) module);
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