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

import java.util.Map;
import java.util.logging.Logger;

import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.context.AbstractServiceContext;
import net.osgi.jxse.factory.FactoryEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Directives;
import net.osgi.jxse.factory.ICompositeFactoryListener;
import net.osgi.jxse.network.NetworkConfigurationFactory;
import net.osgi.jxse.seeds.AbstractResourceSeedListFactory;
import net.osgi.jxse.seeds.ISeedListFactory;
import net.osgi.jxse.service.network.NetPeerGroupService;
import net.osgi.jxse.utils.Utils;

public class XMLServiceContext extends AbstractServiceContext<NetworkManager> implements ICompositeFactoryListener{

	public static final String S_ERR_NO_SERVICE_LOADED = "\n\t!!! No service is loaded. Not starting context:  ";
	
	private IComponentFactory<NetworkManager> factory;
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

	public XMLServiceContext( IComponentFactory<NetworkManager> factory ) {
		super( factory, true );
	}

	public XMLServiceContext( IComponentFactory<NetworkManager> factory, AbstractResourceSeedListFactory seeds ) {
		super( factory );
		this.seeds = seeds;
	}

	@Override
	protected boolean onSetAvailable( IComponentFactory<NetworkManager> factory) {
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
			super.setIdentifier( xmlFactory.getIdentifier());
			Map<Directives, String> directives = xmlFactory.getDirectives();
			String str = directives.get( Directives.PASS1 );
			if( !Utils.isNull(str))
				super.putProperty( Directives.PASS1, str);
			str = directives.get( Directives.PASS2 );
			if( !Utils.isNull(str))
				super.putProperty( Directives.PASS2, str);
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
				this.service.start();
				addChild(this.service);
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
	public void notifyFactoryCreated(FactoryEvent event) {
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
