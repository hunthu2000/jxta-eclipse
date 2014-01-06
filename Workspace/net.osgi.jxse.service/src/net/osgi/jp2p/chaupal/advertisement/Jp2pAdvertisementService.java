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
import net.osgi.jp2p.component.IJp2pComponentNode;
import net.osgi.jp2p.container.AbstractServiceContainer;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementProperties;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.jxta.advertisement.IAdvertisementProvider;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class Jp2pAdvertisementService extends AbstractJp2pServiceNode<Advertisement> implements IJp2pComponentNode<Advertisement>{

	private IComponentChangedListener listener;
	private ChaupalDiscoveryService discovery;
	
	public Jp2pAdvertisementService( IJp2pWritePropertySource<IJp2pProperties> source, Advertisement advertisement, ChaupalDiscoveryService discovery ) {
		super( source, advertisement );
		this.discovery = discovery;
	}
	
	protected void publishAdvertisements( IAdvertisementProvider provider ){
		long lifetime = (long) super.getPropertySource().getProperty( AdvertisementProperties.LIFE_TIME );
		long expiration = (long) super.getPropertySource().getProperty( AdvertisementProperties.EXPIRATION );
		try {
			Advertisement[] advertisements = discovery.getAdvertisements();
			if(( advertisements == null ) || ( advertisements.length == 0 ))
				return;
			System.out.println("Publishing the following advertisement with lifetime :"
					+ lifetime + " expiration :" + expiration);
			Advertisement ad = advertisements[0];
			System.out.println(ad.toString());
			discovery.getModule().publish(ad, lifetime, expiration);
			discovery.getModule().remotePublish(ad, expiration);
		} catch (Exception e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
		}		
	}
	
	/**
	 * Check for advertisements of the designated type
	 */
	protected void checkAdvertisements(){
		DiscoveryPropertySource source = (DiscoveryPropertySource) discovery.getPropertySource();
		String adv_type = super.getPropertySource().getDirective(AdvertisementDirectives.TYPE);
		if( Utils.isNull( adv_type ))
			adv_type = AdvertisementTypes.ADV.toString();
		source.setProperty( DiscoveryProperties.ADVERTISEMENT_TYPE , AdvertisementTypes.valueOf( StringStyler.styleToEnum( adv_type )));
		Advertisement[] advertisements = discovery.getAdvertisements();
		if(( advertisements != null ) && ( advertisements.length > 0 ))
			return;
		ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
		if( this.listener == null ){
			this.listener = new IComponentChangedListener(){

				@Override
				public void notifyServiceChanged(ComponentChangedEvent event) {
					if( event.getSource().equals( discovery )){
						if( event.getChange().equals( AbstractServiceContainer.ServiceChange.COMPONENT_EVENT )){
							publishAdvertisements( (IAdvertisementProvider) discovery );
						}
					}
				}	
			};
			dispatcher.addServiceChangeListener(listener);
		}
	}
	
	@Override
	public boolean start() {
		this.checkAdvertisements();
		return super.start();
	}

	@Override
	protected void deactivate() {
		ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
		dispatcher.removeServiceChangeListener(listener);
		listener = null;
	}
}