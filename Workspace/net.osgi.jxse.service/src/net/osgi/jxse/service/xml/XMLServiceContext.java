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

import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.context.AbstractServiceContext;
import net.osgi.jxse.context.CompositeStarter;
import net.osgi.jxse.context.Swarm;
import net.osgi.jxse.factory.ComponentFactoryEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.service.core.AbstractJxseService;
import net.osgi.jxse.service.network.NetPeerGroupService;
import net.osgi.jxse.service.peergroup.IPeerGroupProperties.PeerGroupProperties;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;

public class XMLServiceContext extends AbstractServiceContext<NetworkManager,ContextProperties, IJxseDirectives>{

	public static final String S_ERR_NO_SERVICE_LOADED = "\n\t!!! No service is loaded. Not starting context:  ";
	public static final String S_ERR_CONTEXT_NOT_BUILT = "\n\t!!! The context was not built! Not starting context:  ";
	
	private NetPeerGroupService service;
	private XMLServiceContext host;
	private ICompositeBuilderListener observer;
	private ComponentNode<NetworkManager,ContextProperties, IJxseDirectives> root;
	
	private String plugin_id;
	private Class<?> clss;
	private Swarm swarm;
	
	public XMLServiceContext( String plugin_id, Class<?> clss) {
		this.host = this;
		this.plugin_id = plugin_id;
		this.clss = clss;	
		this.swarm = new Swarm();
		super.setStatus( Status.AVAILABLE);
	}
	
	@Override
	public IJxsePropertySource<ContextProperties, IJxseDirectives> getProperties() {
		return super.getProperties();
	}

	@Override
	public NetworkManager getModule() {
		if( service == null )
			return null;
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
		XMLFactoryBuilder builder = new XMLFactoryBuilder( plugin_id, clss );
		ICompositeBuilderListener listener = new ICompositeBuilderListener(){

			@Override
			public void notifyCreated(ComponentFactoryEvent event) {
				FactoryEvents fe = event.getFactoryEvent();
				switch( fe ){
				case FACTORY_CREATED:
					break;
				default:
					break;
				}
			}
		};
		builder.addListener(listener);
		if( this.observer != null )
			builder.addListener(observer);

		root = builder.build();
		builder.removeListener(listener);
		super.setProperties( (IJxseWritePropertySource<net.osgi.jxse.context.IJxseServiceContext.ContextProperties, IJxseDirectives>) root.getFactory().getPropertySource() );
		if( this.observer != null )
			builder.removeListener(observer);
		super.setIdentifier( builder.getPropertySource().getIdentifier() );
		super.putProperty( ContextProperties.PASS_1, 
				builder.getPropertySource().getProperty( ContextProperties.PASS_1 ));
		super.putProperty( ContextProperties.PASS_2, 
				builder.getPropertySource().getProperty( ContextProperties.PASS_2 ));
		boolean autostart = Boolean.parseBoolean( (String) root.getFactory().getPropertySource().getDirective( IJxseDirectives.Directives.AUTO_START ));
		return autostart;
	}

	/**
	 * Prepare the context by running the factories
	 * @return
	 */
	private boolean prepare(){
		Logger logger = Logger.getLogger( this.getClass().getName() );
		if( this.root == null ){
			logger.severe( S_ERR_CONTEXT_NOT_BUILT + super.getIdentifier() + " !!!\n");
			return false;
		}
		CompositeStarter<NetworkManager, ContextProperties, IJxseDirectives> starter = 
				new CompositeStarter<NetworkManager, ContextProperties, IJxseDirectives>( this.root );
		ICompositeBuilderListener listener = new ICompositeBuilderListener(){

			@SuppressWarnings("unchecked")
			@Override
			public void notifyCreated(ComponentFactoryEvent event) {
				FactoryEvents fe = event.getFactoryEvent();
				switch( fe ){
				case COMPONENT_CREATED:
					Object component = event.getFactory().getModule();
					if( component instanceof NetworkConfigurator ){
						break;
					}
					if( component instanceof NetworkManager ){
						XMLServiceContext.addModule( host, component );
						service = new NetPeerGroupService( (IComponentFactory<PeerGroup, PeerGroupProperties, IJxseDirectives>) event.getFactory() );						break;
					}
					XMLServiceContext.addModule( host, component );
					if( event.getFactory() instanceof IPeerGroupProvider ){
						IPeerGroupProvider provider = ( IPeerGroupProvider )event.getFactory();
						swarm.addPeerGroup( provider.getPeerGroup() );
					}
					if( component instanceof AbstractJxseService ){
						AbstractJxseService<?,?,?> service = ( AbstractJxseService<?,?,?> )component;
						service.start();
					}
					break;
				default:
					break;
				}
			}
		};
		starter.addListener(listener);
		starter.start();
		//This will happen if the network needs to start before new services can be added.
		if( !starter.isCompleted() ){
			starter.start();
		}
		starter.removeListener(listener);	
		this.root = null;
		return true;
	}
	
	@Override
	protected void activate() {
		Logger logger = Logger.getLogger( this.getClass().getName() );
		if( !this.prepare())
			return;
		try{
			this.service.start();
			addChild(this.service);
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		if( this.service == null ){
			logger.severe( S_ERR_NO_SERVICE_LOADED + super.getIdentifier() + " !!!\n");
			return;
		}
		super.activate();
	}
	
	@Override
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	protected void onFinalising() {	
		this.stop();
	}
}