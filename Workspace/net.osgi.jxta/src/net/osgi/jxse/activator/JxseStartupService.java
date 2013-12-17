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

import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.IJxseComponentNode;
import net.osgi.jxse.context.CompositeStarter;
import net.osgi.jxse.context.JxseServiceContext;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.netpeergroup.NetPeerGroupService;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;

public class JxseStartupService extends AbstractActivator implements IJxseService<NetworkManager,IJxseProperties>{

	public static final String S_ERR_NO_SERVICE_LOADED = "\n\t!!! No service is loaded. Not starting context:  ";
	public static final String S_ERR_CONTEXT_NOT_BUILT = "\n\t!!! The context was not built! Not starting context:  ";
	public static final String S_INFO_AUTOSTART = "\n\t!!! Autostarting container:  ";
	
	private JxseStartupPropertySource source;
	
	private ComponentNode<JxseServiceContext, IJxseProperties> root;
	private NetworkManager manager;
	
	public JxseStartupService( ComponentNode<JxseServiceContext, IJxseProperties> root, JxseStartupPropertySource source ) {
		this.source = source;
		this.root = root;
	}

	/**
	 * If true, the service is autostarted
	 * @return
	 */
	protected boolean isAutoStart(){
		return Boolean.parseBoolean( (String) this.source.getDirective( IJxseDirectives.Directives.AUTO_START ));		
	}
	
	/**
	 * Prepare the context by running the factories
	 * @return
	 */
	private boolean prepare(){
		IJxseWritePropertySource<?> writeRoot = (IJxseWritePropertySource<?>) this.root.getFactory().getPropertySource();
		if( JxseStartupPropertySource.isAutoStart( this.source ) && 
				!JxseStartupPropertySource.isAutoStart( writeRoot ))
			writeRoot.setDirective( Directives.AUTO_START, Boolean.TRUE.toString());
			
		String identifier = source.getIdentifier();
		Logger logger = Logger.getLogger( this.getClass().getName() );
		logger.info( S_INFO_AUTOSTART + identifier + ": " + this.isAutoStart());
		if( this.root == null ){
			logger.severe( S_ERR_CONTEXT_NOT_BUILT + identifier + " !!!\n");
			return false;
		}
		
		CompositeStarter<JxseServiceContext, IJxseProperties> starter = 
				new CompositeStarter<JxseServiceContext, IJxseProperties>( this.root );
		ICompositeBuilderListener<?> listener = new ICompositeBuilderListener<Object>(){

			@Override
			public void notifyCreated(ComponentBuilderEvent<Object> event) {
				JxseServiceContext service = root.getFactory().getModule();
				BuilderEvents fe = event.getBuilderEvent();
				switch( fe ){
				case COMPONENT_CREATED:
					ComponentNode<?,?> node = (ComponentNode<?, ?>) event.getComponent();
					Object component = node.getFactory().getModule();
					if( component.equals( service )){
						break;
					}
					if( component instanceof NetworkConfigurator ){
						break;
					}
					if( component instanceof NetPeerGroupService ){
						NetworkManager manager = ( NetworkManager)component;
						NetPeerGroupService npg = (NetPeerGroupService) component;
						npg.start();
						JxseServiceContext.addModule( service, npg );
					}
					ComponentNode<?,?> parentNode = node.getParent();
					if( parentNode == null )
						break;
					Object po = node.getParent().getFactory().getModule();
					if(!( po instanceof IJxseComponentNode ))
						break;
					IJxseComponentNode<?,?> parent = (IJxseComponentNode<?, ?>) po;
					if( parent.equals( service ))
						JxseServiceContext.addModule( service, component );
					else
						parent.addChild((IJxseComponent<?, ?>) component);
					break;
				default:
					break;
				}
			}
		};
		starter.addListener(listener);
		starter.start();
		starter.removeListener(listener);	
		return true;
	}
	
	/**
	 * Perform the activation
	 */
	public void activate() {
		Logger logger = Logger.getLogger( this.getClass().getName() );
		if( !this.prepare())
			return;
		JxseServiceContext service = root.getFactory().getModule();
		try{
			service.start();
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		if( service == null ){
			String identifier = source.getIdentifier();
			logger.severe( S_ERR_NO_SERVICE_LOADED + identifier + " !!!\n");
			return;
		}
	}
	
	//Make public
	@Override
	public void deactivate() {
		try{
			manager.stopNetwork();
		}
		catch( Exception ex ){
			ex.printStackTrace();
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
	public NetworkManager getModule() {
		return this.manager;
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