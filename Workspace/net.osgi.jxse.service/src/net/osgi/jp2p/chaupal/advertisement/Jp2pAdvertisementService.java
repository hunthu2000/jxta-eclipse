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
package net.osgi.jp2p.chaupal.advertisement;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.document.Advertisement;
import net.osgi.jp2p.chaupal.discovery.ChaupalDiscoveryService;
import net.osgi.jp2p.component.AbstractJp2pServiceNode;
import net.osgi.jp2p.component.ComponentChangedEvent;
import net.osgi.jp2p.component.ComponentEventDispatcher;
import net.osgi.jp2p.component.IComponentChangedListener;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.component.IJp2pComponentNode;
import net.osgi.jp2p.container.AbstractServiceContainer;
import net.osgi.jp2p.jxta.advertisement.IAdvertisementProvider;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class Jp2pAdvertisementService extends AbstractJp2pServiceNode<Advertisement> implements IJp2pComponentNode<Advertisement>{

	private IComponentChangedListener listener;

	public Jp2pAdvertisementService( IJp2pWritePropertySource<IJp2pProperties> source, Advertisement advertisement ) {
		super( source, advertisement );
	}
	
	
	protected void publishAdvertisements( IAdvertisementProvider provider ){
		//if( this.adfactories.isEmpty() )
		//	return;
		//DiscoveryService discovery = super.getModule();
		int lifetime = 100;//( Integer )this.getProperty( PublishProperties.LIFE_TIME );
		int expiration = 100;//= ( Integer )this.getProperty( PublishProperties.EXPIRATION );
		try {
			//for( Advertisement ad: super.getAdvertisements() ){
				System.out.println("Publishing the following advertisement with lifetime :"
						+ lifetime + " expiration :" + expiration);
				//System.out.println(ad.toString());
				//discovery.publish(ad, lifetime, expiration);
				//discovery.remotePublish(ad, expiration);
			//}
		} catch (Exception e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
		}		
	}
	
	@Override
	public boolean start() {
		final ChaupalDiscoveryService service = getDiscoveryService( this );
		if( service != null ){
			ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
			if( this.listener == null ){
				this.listener = new IComponentChangedListener(){

					@Override
					public void notifyServiceChanged(ComponentChangedEvent event) {
						if( event.getSource().equals( service )){
							if( event.getChange().equals( AbstractServiceContainer.ServiceChange.COMPONENT_EVENT )){
								publishAdvertisements( (IAdvertisementProvider) service );
							}
						}
					}	
				};
				dispatcher.addServiceChangeListener(listener);
			}
		}
		return super.start();
	}

	@Override
	protected void deactivate() {
		ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
		dispatcher.removeServiceChangeListener(listener);
		listener = null;
	}

	/**
	 * Get the discovery service
	 * @param adService
	 * @return
	 */
	public static ChaupalDiscoveryService getDiscoveryService( Jp2pAdvertisementService adService ){
		for( IJp2pComponent<?> component: adService.getChildren() ){
			if( component.getModule() instanceof ChaupalDiscoveryService )
				return (ChaupalDiscoveryService) component.getModule();
		}
		return null;
	}
}