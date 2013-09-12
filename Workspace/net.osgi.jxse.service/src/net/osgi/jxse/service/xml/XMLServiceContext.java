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
import net.osgi.jxse.seeds.AbstractResourceSeedListFactory;
import net.osgi.jxse.seeds.ISeedListFactory;
import net.osgi.jxse.service.network.NetPeerGroupService;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.context.IJxseServiceContext.ContextDirectives;

public class XMLServiceContext extends AbstractServiceContext<NetworkManager,ContextProperties, ContextDirectives> implements ICompositeBuilderListener{

	public static final String S_ERR_NO_SERVICE_LOADED = "\n\t!!! No service is loaded. Not starting context:  ";
	
	private IComponentFactory<NetworkManager, ContextProperties, ContextDirectives> factory;
	private NetPeerGroupService service;
	private AbstractResourceSeedListFactory seeds;
	
	public XMLServiceContext( String plugin_id, Class<?> clss) {
		super( new XMLComponentFactory( plugin_id, clss ));
		this.initialise();
	}

	public XMLServiceContext( String plugin_id, Class<?> clss, AbstractResourceSeedListFactory seeds ) {
		this( new XMLComponentFactory( plugin_id, clss ));
		this.seeds = seeds;
	}

	public XMLServiceContext( IComponentFactory<NetworkManager, ContextProperties, ContextDirectives> factory ) {
		super( factory, true );
	}

	public XMLServiceContext( IComponentFactory<NetworkManager, ContextProperties, ContextDirectives> factory, AbstractResourceSeedListFactory seeds ) {
		super( factory );
		this.seeds = seeds;
	}

	@Override
	protected boolean onSetAvailable( IComponentFactory<NetworkManager, ContextProperties, ContextDirectives> factory) {
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

	@Override
	protected boolean onInitialising() {
		if( factory instanceof XMLComponentFactory ){
			XMLComponentFactory xmlFactory = (XMLComponentFactory) factory;
			xmlFactory.addListener(this);
			xmlFactory.createModule();
			xmlFactory.removeListener(this);
			super.setIdentifier( xmlFactory.getPropertySource().getComponentName() );
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
		if( factory instanceof XMLComponentFactory ){
			XMLComponentFactory xmlFactory = (XMLComponentFactory) factory;
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

	@Override
	public void notifyFactoryCreated(ComponentFactoryEvent event) {
		FactoryEvents fe = event.getFactoryEvent();
		switch( fe ){
		case FACTORY_CREATED:
			if( event.getFactory() instanceof NetworkConfigurationFactory ){
				if( this.seeds != null )
				  ((NetworkConfigurationFactory) event.getFactory()).addSeedlist( this.seeds );
			}
			break;
		case COMPONENT_CREATED:
			Object component = event.getFactory().getModule();
			if( component instanceof NetworkConfigurator ){
				break;
			}
			if( component instanceof NetworkManager ){
				addModule( this, component );
				this.service = new NetPeerGroupService( (NetworkManager) component );
				break;
			}
			addModule( this, component );
			break;
		}
	}
}
