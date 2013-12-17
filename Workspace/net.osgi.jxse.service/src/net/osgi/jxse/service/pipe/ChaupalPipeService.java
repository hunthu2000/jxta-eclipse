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
package net.osgi.jxse.service.pipe;

import net.jxta.pipe.PipeService;
import net.osgi.jxse.component.AbstractJxseServiceNode;
import net.osgi.jxse.component.ComponentChangedEvent;
import net.osgi.jxse.component.ComponentEventDispatcher;
import net.osgi.jxse.component.IComponentChangedListener;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.IJxseComponentNode;
import net.osgi.jxse.context.AbstractServiceContext;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.service.advertisement.JxseAdvertisementService;
import net.osgi.jxse.service.discovery.JxseDiscoveryService;

public class ChaupalPipeService extends AbstractJxseServiceNode<PipeService, IJxseProperties> implements IJxseComponentNode<PipeService, IJxseProperties>{

	private IComponentChangedListener listener;

	public ChaupalPipeService( IComponentFactory<PipeService, IJxseProperties> factory ) {
		super( null, factory );
	}
		
	@Override
	public boolean start() {
		JxseAdvertisementService service = getAdvertisementService( this );
		if( service != null ){
			ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
			this.listener = new IComponentChangedListener(){

				@Override
				public void notifyServiceChanged(ComponentChangedEvent event) {
					JxseDiscoveryService service = (JxseDiscoveryService) event.getSource();
					if( event.getSource().equals( service )){
						if( event.getChange().equals( AbstractServiceContext.ServiceChange.COMPONENT_EVENT )){
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
	public static JxseAdvertisementService getAdvertisementService( ChaupalPipeService adService ){
		for( IJxseComponent<?,?> component: adService.getChildren() ){
			if( component.getModule() instanceof JxseAdvertisementService )
				return (JxseAdvertisementService) component.getModule();
		}
		return null;
	}
}