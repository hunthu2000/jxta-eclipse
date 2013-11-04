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
package net.osgi.jxse.service.xml;

import java.util.logging.Logger;

import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.context.AbstractServiceContext;
import net.osgi.jxse.factory.ComponentFactoryEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.service.network.NetPeerGroupService;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;

public class XMLServiceContext extends AbstractServiceContext<NetworkManager,ContextProperties, IJxseDirectives>{

	public static final String S_ERR_NO_SERVICE_LOADED = "\n\t!!! No service is loaded. Not starting context:  ";
	
	private IComponentFactory<NetworkManager, ContextProperties, IJxseDirectives> factory;
	private NetPeerGroupService service;
	private XMLServiceContext host;
	private ICompositeBuilderListener observer;
	
	public XMLServiceContext( String plugin_id, Class<?> clss) {
		super( new XMLComponentBuilder( plugin_id, clss ));
		this.host = this;
	}

	public XMLServiceContext( IComponentFactory<NetworkManager, ContextProperties, IJxseDirectives> factory ) {
		super( factory, true );
		this.host = this;
	}

	@Override
	protected boolean onSetAvailable( IComponentFactory<NetworkManager, ContextProperties, IJxseDirectives> factory) {
		if( !factory.canCreate() )
			return false;
		this.factory = factory;	
		return super.onSetAvailable( factory );
	}
	
	@Override
	public IJxsePropertySource<ContextProperties, IJxseDirectives> getProperties() {
		return super.getProperties();
	}

	@Override
	public NetworkManager getModule() {
		return service.getNetworkManager();
	}

	public ICompositeBuilderListener getObserver() {
		return observer;
	}

	public void setObserver(ICompositeBuilderListener observer) {
		this.observer = observer;
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	protected boolean onInitialising() {
		if( factory instanceof XMLComponentBuilder ){
			XMLComponentBuilder xmlFactory = (XMLComponentBuilder) factory;
			ICompositeBuilderListener listener = new ICompositeBuilderListener(){

				@Override
				public void notifyCreated(ComponentFactoryEvent event) {
					FactoryEvents fe = event.getFactoryEvent();
					switch( fe ){
					case FACTORY_CREATED:
						break;
					case COMPONENT_CREATED:
						Object component = event.getFactory().getModule();
						if( component instanceof NetworkConfigurator ){
							break;
						}
						if( component instanceof NetworkManager ){
							XMLServiceContext.addModule( host, component );
							service = new NetPeerGroupService( (NetworkManager) component );
							break;
						}
						XMLServiceContext.addModule( host, component );
						break;
					}
				}
			};
			xmlFactory.addListener(listener);
			if( this.observer != null )
				xmlFactory.addListener(observer);
				
			xmlFactory.createModule();
			xmlFactory.removeListener(listener);
			if( this.observer != null )
				xmlFactory.removeListener(observer);
			super.setIdentifier( xmlFactory.getPropertySource().getIdentifier() );
			super.putProperty( ContextProperties.PASS_1, 
					xmlFactory.getPropertySource().getProperty( ContextProperties.PASS_1 ));
			super.putProperty( ContextProperties.PASS_2, 
					xmlFactory.getPropertySource().getProperty( ContextProperties.PASS_2 ));
		}
		return true;
	}

	
	@Override
	protected void activate() {
		if( this.service == null ){
			Logger logger = Logger.getLogger( this.getClass().getName() );
			logger.severe( S_ERR_NO_SERVICE_LOADED + super.getIdentifier() + " !!!\n");
			return;
		}
		if( factory instanceof XMLComponentBuilder ){
			XMLComponentBuilder xmlFactory = (XMLComponentBuilder) factory;
			if( xmlFactory.isAutostart()){
				try{
				  this.service.start();
				  addChild(this.service);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		}
		factory = null;
		super.activate();
	}

	@Override
	protected void onFinalising() {
		this.stop();
	}
}
