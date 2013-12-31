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
package net.osgi.jp2p.service.discovery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.osgi.jp2p.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.advertisement.IAdvertisementProvider;
import net.osgi.jp2p.component.AbstractJp2pServiceNode;
import net.osgi.jp2p.component.ComponentChangedEvent;
import net.osgi.jp2p.component.ComponentEventDispatcher;
import net.osgi.jp2p.context.AbstractServiceContainer;
import net.osgi.jp2p.discovery.DiscoveryPropertySource.DiscoveryMode;
import net.osgi.jp2p.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jp2p.log.JxseLevel;
import net.osgi.jp2p.properties.AbstractJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class ChaupalDiscoveryService extends AbstractJp2pServiceNode<DiscoveryService> implements IAdvertisementProvider{
	
	private ExecutorService executor;
	private Runnable runnable;
	
	private DiscoveryListener listener;
	private int size;
	private ChaupalDiscoveryService service;

	public ChaupalDiscoveryService( IJp2pWritePropertySource<IJp2pProperties> source, DiscoveryService discoveryService ) {
		super( source, discoveryService );
		this.size = 0;
		this.service = this;
		executor = Executors.newSingleThreadExecutor();
	}

	
	@Override
	public synchronized Advertisement[] getAdvertisements() {
		Collection<Advertisement> advertisements = discovery( false );
		return advertisements.toArray( new Advertisement[advertisements.size()]);
	}

	/**
	 * Implement pure discovery
	 */
	protected synchronized Collection<Advertisement> discovery( boolean remote ) {
		DiscoveryService discovery = super.getModule();
		Collection<Advertisement> advertisements = new ArrayList<Advertisement>();
		try {
			String peerId = ( String )this.getProperty( DiscoveryProperties.PEER_ID );
			String attribute = ( String )this.getProperty( DiscoveryProperties.ATTRIBUTE );
			String wildcard = ( String )this.getProperty( DiscoveryProperties.WILDCARD );
			int threshold = ( Integer )this.getProperty( DiscoveryProperties.THRESHOLD );
			int adType = AdvertisementTypes.convertForDiscovery(( AdvertisementTypes) this.getProperty( DiscoveryProperties.ADVERTISEMENT_TYPE ));
			Enumeration<Advertisement> enm= discovery.getLocalAdvertisements( adType, attribute, wildcard );
			while( enm.hasMoreElements()){
				advertisements.add(enm.nextElement());
			}
			if( remote )
				discovery.getRemoteAdvertisements( peerId,  adType, attribute, wildcard, threshold, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return advertisements;
	}

	/**
	 * Get the size of the advertisements that were found
	 * @return
	 */
	public int getSize() {
		return size;
	}
		
	/**
	 * The activities performed in an active state. By default this is discovery
	 */
	protected void onActiveState(){
		this.discovery( true);		
	}

	/**
	 * even though a counter is used, the discovery mode determines which count value is used:
	 * - continuous: no stop
	 * - one-shot: only once
	 * - count (default): countdown
	 * 
	 * @return
	 */
	protected int getCount(){
		int count = ( Integer )getProperty( DiscoveryProperties.COUNT );
		DiscoveryMode mode = (DiscoveryMode) getProperty( DiscoveryProperties.MODE );
		switch( mode ){
		case CONTINUOUS:
			return -1;
		case ONE_SHOT:
			return 1;
		default:
			return count;
		}
	}
	
	@Override
	public boolean start() {
		boolean retval = super.start();
		DiscoveryService discovery = super.getModule();
		listener = new DiscoveryListener(){

			@Override
			public void discoveryEvent(DiscoveryEvent event) {
				DiscoveryResponseMsg res = event.getResponse();
				// let's get the responding peer's advertisement
				System.out.println(" [ Got a Discovery Response [" +
						res.getResponseCount() + " elements] from peer : " +
						event.getSource() + " ]");
				Advertisement adv;
				Enumeration<?> en = res.getAdvertisements();
				size = 0;
				if (en != null) {
					while (en.hasMoreElements()) {
						adv = (Advertisement) en.nextElement();
						size++;
						System.out.println(adv);
					}
				}
				getProperties().setProperty( DiscoveryProperties.FOUND, size );
			}	
		};
		discovery.addDiscoveryListener(listener);
		this.runnable = new Runnable(){
			@Override
			public void run() {
				int wait_time = ( Integer )getProperty( DiscoveryProperties.WAIT_TIME );
				int count = getCount();
				getProperties().getOrCreateManagedProperty( DiscoveryProperties.COUNTER, AbstractJp2pPropertySource.S_RUNTIME, false );
				while (( isActive() ) && ( count > 0 )) {
					onActiveState();
					try {
						Thread.sleep(wait_time);
					} catch (Exception e) {
						Logger log = Logger.getLogger( this.getClass().getName() );
						log.log( JxseLevel.JXSELEVEL, this.getClass().getSimpleName() + "Interrupted" );
					}
					if( count > 0 )
						count--;
					getProperties().setProperty( DiscoveryProperties.COUNTER, count);
					ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
					dispatcher.serviceChanged( new ComponentChangedEvent( service, AbstractServiceContainer.ServiceChange.COMPONENT_EVENT ));
				}
				stop();
			}		
		};
		this.executor.execute(runnable);
		return retval;
	}

	@Override
	protected void deactivate() {
		Thread.currentThread().interrupt();
		DiscoveryService discovery = super.getModule();
		if( listener != null)
			discovery.removeDiscoveryListener( listener );
		this.listener = null;
	}
}