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
package net.osgi.jxse.service.advertisement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementProperties;
import net.osgi.jxse.advertisement.JxseAdvertisementFactory;
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.discovery.DiscoveryPropertySource.DiscoveryMode;
import net.osgi.jxse.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jxse.discovery.DiscoveryServiceFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.service.core.AbstractJxseService;
import net.osgi.jxse.service.discovery.JxseDiscoveryService;
import net.osgi.jxse.utils.StringStyler;

public class JxseAdvertisementService extends AbstractJxseService<Advertisement, AdvertisementProperties, IJxseDirectives>{

	public JxseAdvertisementService( ComponentNode<Advertisement, AdvertisementProperties, IJxseDirectives> node ) {
		super( factory );
	}
	
	@Override
	protected boolean onInitialising() {
		for( JxseAdvertisementFactory factory: this.adfactories ){
			Advertisement ad = factory.createModule();
			if( ad != null )
				super.addAdvertisement( ad);
		}	
		return super.onInitialising();
	}

	protected void publishAdvertisements(){
		if( this.adfactories.isEmpty() )
			return;
		DiscoveryService discovery = super.getModule();
		int lifetime = ( Integer )this.getProperty( PublishProperties.LIFE_TIME );
		int expiration = ( Integer )this.getProperty( PublishProperties.EXPIRATION );
		try {
			for( Advertisement ad: super.getAdvertisements() ){
				System.out.println("Publishing the following advertisement with lifetime :"
						+ lifetime + " expiration :" + expiration);
				System.out.println(ad.toString());
				discovery.publish(ad, lifetime, expiration);
				discovery.remotePublish(ad, expiration);
			}
		} catch (Exception e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
		}		
	}
	

	@Override
	protected void onActiveState() {
		DiscoveryMode mode = ( DiscoveryMode )this.getProperty( DiscoveryProperties.DISCOVERY_MODE );
		if(!( mode.equals( DiscoveryMode.DISCOVERY )))
		  this.publishAdvertisements();		
		super.onActiveState();
	}

	@Override
	protected void deactivate() {
		// TODO Auto-generated method stub
		
	}
}