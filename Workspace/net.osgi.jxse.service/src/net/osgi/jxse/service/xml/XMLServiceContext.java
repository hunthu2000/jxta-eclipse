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
import net.osgi.jxse.network.NetworkConfigurationFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.seeds.AbstractResourceSeedListFactory;
import net.osgi.jxse.seeds.ISeedListFactory;
import net.osgi.jxse.service.network.NetPeerGroupService;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;

public class XMLServiceContext extends AbstractServiceContext<NetworkManager,ContextProperties, IJxseDirectives.Directives>{

	public static final String S_ERR_NO_SERVICE_LOADED = "\n\t!!! No service is loaded. Not starting context:  ";
	
	private IComponentFactory<NetworkManager, ContextProperties, IJxseDirectives.Directives> factory;
	private NetPeerGroupService service;
	private AbstractResourceSeedListFactory seeds;
	private XMLServiceContext host;
	private ICompositeBuilderListener observer;
	
	public XMLServiceContext( String plugin_id, Class<?> clss) {
		super( new XMLComponentBuilder( plugin_id, clss ));
		this.host = this;
	}

	public XMLServiceContext( String plugin_id, Class<?> clss, AbstractResourceSeedListFactory seeds ) {
		this( new XMLComponentBuilder( plugin_id, clss ));
		this.seeds = seeds;
	}

	public XMLServiceContext( IComponentFactory<NetworkManager, ContextProperties, IJxseDirectives.Directives> factory ) {
		super( factory, true );
		this.host = this;
	}

	public XMLServiceContext( IComponentFactory<NetworkManager, ContextProperties, IJxseDirectives.Directives> factory, AbstractResourceSeedListFactory seeds ) {
		super( factory );
		this.host = this;
		this.seeds = seeds;
	}

	@Override
	protected boolean onSetAvailable( IComponentFactory<NetworkManager, ContextProperties, IJxseDirectives.Directives> factory) {
		if( !factory.canCreate() )
			return false;
		this.factory = factory;	
		return super.onSetAvailable( factory );
	}

	@Override
	public NetworkManager getModule() {
		return service.getNetworkManager();
	}

	ISeedListFactory getSeeds() {
		return seeds;
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
						if( event.getFactory() instanceof NetworkConfigurationFactory ){
							if( seeds != null )
							  ((NetworkConfigurationFactory) event.getFactory()).addSeedlist( seeds );
						}
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
