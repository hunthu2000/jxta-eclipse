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
package net.osgi.jxse.service.discovery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.osgi.jxse.advertisement.AbstractAdvertisementFactory;
import net.osgi.jxse.utils.StringStyler;

public class JxsePublishService extends JxseDiscoveryService{

	public enum PublishProperties{
		LIFE_TIME,
		EXPIRATION;

	@Override
	public String toString() {
		return StringStyler.prettyString( super.toString() );
	}}

	private Collection<AbstractAdvertisementFactory<?,?,?>> adfactories;
	
	public JxsePublishService( DiscoveryService component ) {
		super( component );
		adfactories = new ArrayList<AbstractAdvertisementFactory<?,?,?>>();
	}

	@Override
	protected void fillDefaultValues() {
		this.putProperty( DiscoveryProperties.MODE, Mode.DISCOVERY_AND_PUBLISH, true );
		this.putProperty( PublishProperties.EXPIRATION, 1200000, true );
		this.putProperty( PublishProperties.LIFE_TIME, 120000,true );
		super.fillDefaultValues();
	}

	public void addAdvertisement( AbstractAdvertisementFactory<?,?,?> advertisement ){
		this.adfactories.add(advertisement);
	}

	public void removeAdvertisement( AbstractAdvertisementFactory<?,?,?> advertisement ){
		this.adfactories.remove(advertisement);
	}
	
	@Override
	protected boolean onInitialising() {
		for( AbstractAdvertisementFactory<?,?,?> factory: this.adfactories ){
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
		Mode mode = ( Mode )this.getProperty( DiscoveryProperties.MODE );
		if(!( mode.equals( Mode.DISCOVERY )))
		  this.publishAdvertisements();		
		super.onActiveState();
	}
}