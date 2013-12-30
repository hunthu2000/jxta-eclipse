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

import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jxse.component.AbstractJxseService;
import net.osgi.jxse.component.ComponentChangedEvent;
import net.osgi.jxse.component.ComponentEventDispatcher;
import net.osgi.jxse.context.AbstractServiceContext;
import net.osgi.jxse.discovery.DiscoveryPropertySource.DiscoveryMode;
import net.osgi.jxse.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jxse.log.JxseLevel;
import net.osgi.jxse.properties.AbstractJxsePropertySource;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class ChaupalDiscoveryService extends AbstractJxseService<DiscoveryService>{
	
	private ExecutorService executor;
	private Runnable runnable;
	
	private DiscoveryListener listener;
	private int size;
	private ChaupalDiscoveryService service;

	public ChaupalDiscoveryService( IJxseWritePropertySource<IJxseProperties> source, DiscoveryService discoveryService ) {
		super( source, discoveryService );
		this.size = 0;
		this.service = this;
		executor = Executors.newSingleThreadExecutor();
	}

	/**
	 * Implement pure discovery
	 */
	protected void discovery() {
		DiscoveryService discovery = super.getModule();
		try {
			String peerId = ( String )this.getProperty( DiscoveryProperties.PEER_ID );
			String attribute = ( String )this.getProperty( DiscoveryProperties.ATTRIBUTE );
			String wildcard = ( String )this.getProperty( DiscoveryProperties.WILDCARD );
			int threshold = ( Integer )this.getProperty( DiscoveryProperties.THRESHOLD );
			int adType = AdvertisementTypes.convertForDiscovery(( AdvertisementTypes) this.getProperty( DiscoveryProperties.ADVERTISEMENT_TYPE ));
			Enumeration<Advertisement> enm= discovery.getLocalAdvertisements( adType, attribute, wildcard );
			this.calculateSize(enm);
			discovery.getRemoteAdvertisements( peerId,  adType, attribute, wildcard, threshold, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the size of the advertisements that were found
	 * @return
	 */
	public int getSize() {
		return size;
	}

	protected synchronized void calculateSize( Enumeration<Advertisement> enm ){
		size = 0;
		while( enm.hasMoreElements() )
			size++;
		this.putProperty( DiscoveryProperties.FOUND, size );
	}
		
	/**
	 * The activities performed in an active state. By default this is discovery
	 */
	protected void onActiveState(){
		this.discovery();		
	}

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
				if (en != null) {
					while (en.hasMoreElements()) {
						adv = (Advertisement) en.nextElement();
						System.out.println(adv);
					}
				}
			}	
		};
		discovery.addDiscoveryListener(listener);
		this.runnable = new Runnable(){
			@Override
			public void run() {
				int wait_time = ( Integer )getProperty( DiscoveryProperties.WAIT_TIME );
				int count = getCount();
				getProperties().getOrCreateManagedProperty( DiscoveryProperties.COUNTER, AbstractJxsePropertySource.S_RUNTIME, false );
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
					dispatcher.serviceChanged( new ComponentChangedEvent( service, AbstractServiceContext.ServiceChange.COMPONENT_EVENT ));
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