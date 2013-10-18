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
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.RegistrationService;
import net.jxta.document.Advertisement;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.osgi.jxse.advertisement.AbstractAdvertisementFactory.AdvertisementTypes;
import net.osgi.jxse.discovery.DiscoveryPropertySource.DiscoveryMode;
import net.osgi.jxse.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jxse.log.JxseLevel;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.service.core.AbstractJxseService;

public class JxseDiscoveryService extends AbstractJxseService<RegistrationService, DiscoveryProperties, IJxseDirectives.Directives> implements Runnable, DiscoveryListener {
	
	private ExecutorService executor;
	
	public JxseDiscoveryService( RegistrationService discoveryService ) {
		super( discoveryService );
		executor = Executors.newSingleThreadExecutor();
	}

	public void putProperty( DiscoveryProperties key, Object value ){
		if( value == null )
			return;
		super.putProperty(key, value );
	}

	protected void putProperty( DiscoveryProperties key, Object value, boolean skipFilled ){
		if( value == null )
			return;
		super.putProperty(key, value, skipFilled );
	}

	public Object getProperty( DiscoveryProperties key ){
		return super.getProperty(key);
	}
	
	@Override
	public Iterator<?> iterator() {
		List<DiscoveryProperties> set = Arrays.asList(DiscoveryProperties.values());
		return set.iterator();
	}

	/**
	 * Implement pure discovery
	 */
	protected void discovery() {
		RegistrationService discovery = super.getModule();
		try {
			String peerId = ( String )this.getProperty( DiscoveryProperties.PEER_ID );
			String attribute = ( String )this.getProperty( DiscoveryProperties.ATTRIBUTE );
			String wildcard = ( String )this.getProperty( DiscoveryProperties.WILDCARD );
			int threshold = ( Integer )this.getProperty( DiscoveryProperties.THRESHOLD );

			String adType = AdvertisementTypes.convert(( AdvertisementTypes) this.getProperty( null /*DiscoveryProperties.ADVERTISEMENT_TYPE*/ ));
			discovery.getLocalAdvertisements( Integer.parseInt( adType ), attribute, wildcard );
			discovery.getRemoteAdvertisements( peerId,  Integer.parseInt( adType ), attribute, wildcard, threshold, null);

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
		RegistrationService discovery = super.getModule();
		Collection<Advertisement> advertisements = new ArrayList<Advertisement>();
		try {
			Enumeration<Advertisement> enm = discovery.getLocalAdvertisements( RegistrationService.ADV, null, null);
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
		DiscoveryMode mode = ( DiscoveryMode )this.getProperty( DiscoveryProperties.DISCOVERY_MODE );
		if(!( mode.equals( DiscoveryMode.PUBLISH )))
		  this.discovery();		
	}

	
	@Override
	public boolean start() {
		RegistrationService discovery = super.getModule();
		discovery.addDiscoveryListener(this);
		this.executor.execute(this);
		return super.start();
	}

	@Override
	public void run() {
		int wait_time = ( Integer )this.getProperty( DiscoveryProperties.WAIT_TIME );
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
		RegistrationService discovery = super.getModule();
		discovery.removeDiscoveryListener(this );
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
	protected void fillDefaultValues() {
		// TODO Auto-generated method stub
		
	}

}