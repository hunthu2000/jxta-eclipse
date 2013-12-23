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

import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.document.Advertisement;
import net.osgi.jxse.component.AbstractJxseServiceNode;
import net.osgi.jxse.component.ComponentChangedEvent;
import net.osgi.jxse.component.ComponentEventDispatcher;
import net.osgi.jxse.component.IComponentChangedListener;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.IJxseComponentNode;
import net.osgi.jxse.context.AbstractServiceContext;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.service.discovery.JxseDiscoveryService;

public class JxseAdvertisementService extends AbstractJxseServiceNode<Advertisement> implements IJxseComponentNode<Advertisement>{

	private IComponentChangedListener listener;

	public JxseAdvertisementService( IComponentFactory<Advertisement> factory ) {
		super( null, factory );
	}
	
	protected void publishAdvertisements(){
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
		JxseDiscoveryService service = getDiscoveryService( this );
		if( service != null ){
			ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
			this.listener = new IComponentChangedListener(){

				@Override
				public void notifyServiceChanged(ComponentChangedEvent event) {
					JxseDiscoveryService service = (JxseDiscoveryService) event.getSource();
					if( event.getSource().equals( service )){
						if( event.getChange().equals( AbstractServiceContext.ServiceChange.STATUS_CHANGE )){
							if( !service.isActive())
								publishAdvertisements();
						}
					}
					
				}
				
			};
			dispatcher.addServiceChangeListener(listener);;
			return service.start();
		}
		else
			return super.start();
	}

	@Override
	protected void deactivate() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Get the discovery service
	 * @param adService
	 * @return
	 */
	public static JxseDiscoveryService getDiscoveryService( JxseAdvertisementService adService ){
		for( IJxseComponent<?,?> component: adService.getChildren() ){
			if( component.getModule() instanceof JxseDiscoveryService )
				return (JxseDiscoveryService) component.getModule();
		}
		return null;
	}
}