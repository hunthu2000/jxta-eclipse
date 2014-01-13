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
import net.osgi.jp2p.activator.ActivatorEvent;
import net.osgi.jp2p.activator.ActivatorListener;
import net.osgi.jp2p.chaupal.discovery.ChaupalDiscoveryService;
import net.osgi.jp2p.component.AbstractJp2pServiceNode;
import net.osgi.jp2p.component.ComponentChangedEvent;
import net.osgi.jp2p.component.ComponentEventDispatcher;
import net.osgi.jp2p.component.IComponentChangedListener;
import net.osgi.jp2p.component.IJp2pComponentNode;
import net.osgi.jp2p.container.AbstractServiceContainer;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementMode;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementProperties;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.Scope;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jp2p.log.Jp2pLevel;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class Jp2pAdvertisementService<T extends Advertisement> extends AbstractJp2pServiceNode<T> implements IJp2pComponentNode<T>{

	private IComponentChangedListener listener;
	private ActivatorListener actListener;;
	private ChaupalDiscoveryService discovery;
	private Jp2pAdvertisementService<T> service;
	
	public Jp2pAdvertisementService( IJp2pWritePropertySource<IJp2pProperties> source, T advertisement, ChaupalDiscoveryService discovery ) {
		super( source, advertisement );
		this.discovery = discovery;
		this.service = this;
	}
	
	/**
	 * Publish the advertisement according to the desired approach
	 * @param provider
	 */
	protected synchronized void publishAdvertisements( Advertisement ad ){
		long lifetime = (long) super.getPropertySource().getProperty( AdvertisementProperties.LIFE_TIME );
		long expiration = (long) super.getPropertySource().getProperty( AdvertisementProperties.EXPIRATION );
		Scope scope = AdvertisementPropertySource.getScope( super.getPropertySource());
		Logger log = Logger.getLogger( this.getClass().getName() );
		log.log( Jp2pLevel.JP2PLEVEL, "Publishing the following advertisement with lifetime :"
				+ lifetime + " expiration :" + expiration);
		try {
			log.log( Jp2pLevel.JP2PLEVEL, ad.toString());
			switch( scope ){
			case LOCAL:
				discovery.getModule().publish(ad, lifetime, expiration);
				break;
			case REMOTE:
				discovery.getModule().publish(ad, lifetime, expiration);
				discovery.getModule().remotePublish(ad, expiration);
				break;
			case REMOTE_ONLY:
				discovery.getModule().remotePublish(ad, expiration);
				break;
			default:
				break;
			}
			ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
			dispatcher.serviceChanged( new ComponentChangedEvent( service, AbstractServiceContainer.ServiceChange.COMPONENT_EVENT ));			
		} catch (Exception e) {
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
		}		
	}

	/**
	 * Activate discovery mode by adding listeners for discovery services
	 */
	protected synchronized void discovery(){
		Advertisement[] advertisements = discovery.getAdvertisements();
		if(( advertisements != null ) && ( advertisements.length > 0 )){
			discovery.pause();
			return;
		}
		DiscoveryPropertySource source = (DiscoveryPropertySource) discovery.getPropertySource();
		String adv_type = super.getPropertySource().getDirective(AdvertisementDirectives.TYPE);
		if( Utils.isNull( adv_type ))
			adv_type = AdvertisementTypes.ADV.toString();
		source.setProperty( DiscoveryProperties.ADVERTISEMENT_TYPE , AdvertisementTypes.valueOf( StringStyler.styleToEnum( adv_type )));
		
		if( this.actListener == null ){
			this.actListener = new ActivatorListener(){

				@Override
				public void notifyStatusChanged(ActivatorEvent event) {
					if( event.getSource().equals( discovery )){
						if( Status.FINALISING.equals( event.getStatus() )){
							Advertisement adv = getModule();
							publishAdvertisements(adv);
						}
					}
				}
			};
		}
		if( this.listener == null ){
			this.listener = new IComponentChangedListener(){

				@Override
				public void notifyServiceChanged(ComponentChangedEvent event) {
					if( event.getSource().equals( discovery )){
						if( event.getChange().equals( AbstractServiceContainer.ServiceChange.COMPONENT_EVENT )){
							Advertisement[] advertisements = discovery.getAdvertisements();
							if(( advertisements == null ) || ( advertisements.length == 0 ))
								return;
							Advertisement ad = advertisements[0];
							publishAdvertisements( ad );
						}
					}
				}	
			};
			ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
			dispatcher.addServiceChangeListener(listener);
		}
	}

	/**
	 * Check for advertisements of the designated type
	 */
	protected void checkAdvertisements(){
		Advertisement adv = super.getModule();
		AdvertisementMode mode = (AdvertisementMode) getPropertySource().getProperty( AdvertisementProperties.MODE );;
		Advertisement[] advertisements = null;
		switch( mode ){
		case DISCOVERY:
			discovery();
			break;
		case DISCOVERY_AND_PUBLISH:
			discovery();
			advertisements = discovery.getAdvertisements();
			if(( advertisements != null ) &&( advertisements.length > 0 ))
				adv = advertisements[0];
			publishAdvertisements( adv );
			break;
		case PUBLISH:
			advertisements = discovery.getAdvertisements();
			if(( advertisements != null ) &&( advertisements.length > 0 ))
				adv = advertisements[0];
			publishAdvertisements( adv );
			break;
		}
	}
	
	@Override
	public boolean start() {
		this.checkAdvertisements();
		return super.start();
	}

	/**
	 * Pause the service by removing all listeners
	 */
	protected void pauseService(){
		if( this.actListener != null )
			discovery.removeActivatorListener( actListener );
		ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
		if( listener != null)
			dispatcher.removeServiceChangeListener(listener);
		actListener = null;
		listener = null;		
	}
	
	@Override
	protected void deactivate() {
		this.pauseService();
	}
}