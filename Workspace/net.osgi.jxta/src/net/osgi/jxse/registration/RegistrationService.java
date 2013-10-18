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
package net.osgi.jxse.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.document.Advertisement;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.osgi.jxse.activator.AbstractActivator;
import net.osgi.jxse.activator.IJxseService;
import net.osgi.jxse.advertisement.AbstractAdvertisementFactory.AdvertisementTypes;
import net.osgi.jxse.discovery.DiscoveryPropertySource.DiscoveryMode;
import net.osgi.jxse.registration.RegistrationPropertySource.RegistrationProperties;
import net.osgi.jxse.log.JxseLevel;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class RegistrationService extends AbstractActivator<IPeerGroupProvider> implements IJxseService<RegistrationService>, Runnable, DiscoveryListener {
	
	private IJxseWritePropertySource<RegistrationProperties, IJxseDirectives> source;
	private ExecutorService executor;
	
	public RegistrationService( IPeerGroupProvider provider, IJxseWritePropertySource<RegistrationProperties, IJxseDirectives> source ) {
		super( provider );
		this.source = source;
		executor = Executors.newSingleThreadExecutor();
	}

	/**
	 * Implement pure discovery
	 */
	protected void discovery() {
		try {
			String peerId = ( String )this.getProperty( RegistrationProperties.PEER_ID );
			String attribute = ( String )this.getProperty( RegistrationProperties.ATTRIBUTE );
			String wildcard = ( String )this.getProperty( RegistrationProperties.WILDCARD );
			int threshold = ( Integer )this.getProperty( RegistrationProperties.THRESHOLD );

			String adType = AdvertisementTypes.convert(( AdvertisementTypes) this.getProperty( null /*DiscoveryProperties.ADVERTISEMENT_TYPE*/ ));
			//discovery.getLocalAdvertisements( Integer.parseInt( adType ), attribute, wildcard );
			//discovery.getRemoteAdvertisements( peerId,  Integer.parseInt( adType ), attribute, wildcard, threshold, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the local cache of the discovery service
	 * @return
	 */
	@Override
	public Advertisement[] getAdvertisements(){
		Collection<Advertisement> advertisements = new ArrayList<Advertisement>();
		try {
			Enumeration<Advertisement> enm = null;//service.getLocalAdvertisements( RegistrationService.ADV, null, null);
			while( enm.hasMoreElements())
				advertisements.add( enm.nextElement());
		} catch (Exception e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
			return null;
		}
		return advertisements.toArray(new Advertisement[advertisements.size()]);
	}
	
	/**
	 * The activities performed in an active state. By defalt this is discovery
	 */
	protected void onActiveState(){
		DiscoveryMode mode = ( DiscoveryMode )this.getProperty( RegistrationProperties.DISCOVERY_MODE );
		if(!( mode.equals( DiscoveryMode.PUBLISH )))
		  this.discovery();		
	}

	
	@Override
	public boolean start() {
		//service.addDiscoveryListener(this);
		this.executor.execute(this);
		return super.start();
	}

	@Override
	public void run() {
		int wait_time = ( Integer )this.getProperty( RegistrationProperties.WAIT_TIME );
		while ( super.isActive()) {
			this.onActiveState();
			try {
				Thread.sleep(wait_time);
			} catch (Exception e) {
				Logger log = Logger.getLogger( this.getClass().getName() );
				log.log( JxseLevel.JXSELEVEL, this.getClass().getSimpleName() + "Interrupted" );
			}
		}
	}
		
	@Override
	protected void deactivate() {
		Thread.currentThread().interrupt();
		//service.removeDiscoveryListener(this );
	}

	@Override
	public void discoveryEvent(DiscoveryEvent event) {
		DiscoveryResponseMsg res = event.getResponse();
		// let's get the responding peer's advertisement
		System.out.println(" [ Got a Discovery Response [" +
				res.getResponseCount() + " elements] from peer : " +
				event.getSource() + " ]");
		Advertisement adv;
		Enumeration<?> en = res.getAdvertisements();
		if (en != null) {
			while (en.hasMoreElements()) {
				adv = (Advertisement) en.nextElement();
				System.out.println(adv);
			}
		}
	}

	@Override
	public String getId() {
		return this.source.getId();
	}

	@Override
	public Date getCreateDate() {
		return null;//this.source.getProperty();
	}

	@Override
	public boolean hasAdvertisements() {
		return false;
	}

	@Override
	public Object getProperty(Object id) {
		return this.source.getProperty((RegistrationProperties) id);
	}

	@Override
	public void putProperty(Object id, Object value) {
		this.source.setProperty((RegistrationProperties) id, value);
	}

	@Override
	public RegistrationService getModule() {
		return null;//this.service;
	}

	@Override
	protected boolean onInitialising() {
		return true;
	}

	@Override
	protected void onFinalising() {
	}

	@Override
	public Iterator<?> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean onSetAvailable(IPeerGroupProvider factory) {
		// TODO Auto-generated method stub
		return false;
	}
}