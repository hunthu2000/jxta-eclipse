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
package net.osgi.jxse.compatibility;

import java.io.IOException;

import org.eclipse.core.runtime.Platform;

import net.jp2p.chaupal.jxta.root.network.NetworkManagerPropertyFacade;
import net.jp2p.chaupal.jxta.root.network.configurator.NetworkConfiguratorPropertyFacade;
import net.jp2p.container.AbstractJp2pContainer;
import net.jp2p.container.IJp2pContainer;
import net.jp2p.container.Jp2pContainerPropertySource;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.component.IJp2pComponentNode;
import net.jp2p.container.component.Jp2pComponentNode;
import net.jp2p.container.properties.DefaultPropertySource;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.utils.Utils;
import net.jp2p.jxta.peergroup.PeerGroupPropertyFacade;
import net.jxta.peergroup.PeerGroup;
import net.jxta.compatibility.platform.NetworkConfigurator;
import net.jxta.compatibility.platform.NetworkManager;

public abstract class AbstractExampleContext extends AbstractJp2pContainer<NetworkManager>{

	private String identifier;
	
	protected AbstractExampleContext( String bundle_id, String identifier ) {
		super(new Jp2pContainerPropertySource( bundle_id ));
		this.identifier = identifier;
	}

	@Override
	protected boolean onInitialising() {
		return true;
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public boolean addChild(IJp2pComponent<?> child) {
		if( child.getModule() instanceof NetworkManager ){
			super.setModule((NetworkManager) child.getModule());
		}
		return super.addChild(child);
	}

	@Override
	protected void onFinalising() {
		this.getModule().stopNetwork();
	}

	/**
	 * Implement the 'main' method
	 * @param args
	 */
	protected abstract void main(String[] args);

	@Override
	public boolean start() {
		String[] args = Platform.getCommandLineArgs();
		main( args );
		return super.start();
	}

	/**
	 * Add a module
	 * @param container
	 * @param module
	 */
	protected static void addModule( IJp2pContainer container, Object module ){
		IJp2pComponent<?> component = getComponent( container, module );
		if( module instanceof NetworkManager){
			NetworkManager manager = (NetworkManager) module;
			try {
				((Jp2pComponentNode<?>) component).addChild( getComponent( container, manager.getConfigurator()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		container.addChild(component);
	}
	
	protected static IJp2pComponent<?> getComponent( IJp2pContainer container, Object module ){
		IJp2pPropertySource<IJp2pProperties> properties = null;
		String bundleId = Jp2pContainerPropertySource.getBundleId( container.getPropertySource() );
		if( module instanceof NetworkManager ){
			NetworkManager manager = (NetworkManager) module;
			if( Utils.isNull( manager.getInstanceName()))
				manager.setInstanceName( container.getIdentifier() );
			properties = new NetworkManagerPropertyFacade( bundleId, (NetworkManager) module );
		}else if( module instanceof NetworkConfigurator ){
			properties =  new NetworkConfiguratorPropertyFacade( bundleId, (NetworkConfigurator)module );
		}else if( module instanceof PeerGroup ){
			properties =  new PeerGroupPropertyFacade( bundleId, (PeerGroup)module );
		}else{
			properties = new DefaultPropertySource( bundleId, module.getClass().getSimpleName());
		}
		return new Jp2pComponentNode<Object>( properties, module );
	}
	/**
	 * Find the component with the given module 
	 * @param componentName
	 * @param root
	 * @return
	 */
	public static IJp2pComponent<?> findComponent( Object module, IJp2pComponent<?> root ){
		if( module == null )
			return null;
		if( module.equals( root.getModule() ))
			return root;
		if(!( root instanceof IJp2pComponentNode ))
			return null;
		IJp2pComponentNode<?> node = ( IJp2pComponentNode<?>) root;
		IJp2pComponentNode<?> result;
		for( IJp2pComponent<?> child: node.getChildren() ){
			result = (IJp2pComponentNode<?>) findComponent( module, child);
			if( result != null )
				return result;
		}
		return null;
	}
	
}
