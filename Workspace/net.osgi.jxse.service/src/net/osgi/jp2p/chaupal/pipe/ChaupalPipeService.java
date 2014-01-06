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
package net.osgi.jp2p.chaupal.pipe;

import net.jxta.pipe.PipeService;
import net.osgi.jp2p.chaupal.advertisement.Jp2pAdvertisementService;
import net.osgi.jp2p.chaupal.discovery.ChaupalDiscoveryService;
import net.osgi.jp2p.component.AbstractJp2pServiceNode;
import net.osgi.jp2p.component.ComponentChangedEvent;
import net.osgi.jp2p.component.ComponentEventDispatcher;
import net.osgi.jp2p.component.IComponentChangedListener;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.container.AbstractServiceContainer;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class ChaupalPipeService extends AbstractJp2pServiceNode<PipeService>{

	private IComponentChangedListener listener;

	public ChaupalPipeService( IJp2pWritePropertySource<IJp2pProperties> source, PipeService pipeService ) {
		super( source, pipeService );
	}
		
	@Override
	public boolean start() {
		Jp2pAdvertisementService service = getAdvertisementService( this );
		if( service != null ){
			ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
			this.listener = new IComponentChangedListener(){

				@Override
				public void notifyServiceChanged(ComponentChangedEvent event) {
					ChaupalDiscoveryService service = (ChaupalDiscoveryService) event.getSource();
					if( event.getSource().equals( service )){
						if( event.getChange().equals( AbstractServiceContainer.ServiceChange.COMPONENT_EVENT )){
							if( !service.isActive())
								notifyComponentChanged( event );
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
	public static Jp2pAdvertisementService getAdvertisementService( ChaupalPipeService adService ){
		for( IJp2pComponent<?> component: adService.getChildren() ){
			if( component.getModule() instanceof Jp2pAdvertisementService )
				return (Jp2pAdvertisementService) component.getModule();
		}
		return null;
	}
}