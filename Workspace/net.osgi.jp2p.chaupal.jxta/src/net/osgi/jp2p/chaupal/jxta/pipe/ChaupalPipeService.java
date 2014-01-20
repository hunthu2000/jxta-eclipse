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
package net.osgi.jp2p.chaupal.jxta.pipe;

import java.io.IOException;

import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jp2p.activator.IJp2pService;
import net.osgi.jp2p.chaupal.jxta.advertisement.Jp2pAdvertisementService;
import net.osgi.jp2p.component.AbstractJp2pServiceNode;
import net.osgi.jp2p.component.ComponentChangedEvent;
import net.osgi.jp2p.component.ComponentEventDispatcher;
import net.osgi.jp2p.component.IComponentChangedListener;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.container.AbstractServiceContainer;
import net.osgi.jp2p.container.AbstractServiceContainer.ServiceChange;
import net.osgi.jp2p.jxta.pipe.PipePropertySource.PipeServiceProperties;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class ChaupalPipeService extends AbstractJp2pServiceNode<PipeService>{

	private IComponentChangedListener listener;
	
	private PipeAdvertisement pipead;
	private IJp2pService<PipeAdvertisement> adService;
	
	public ChaupalPipeService( IJp2pWritePropertySource<IJp2pProperties> source, PipeService pipeService, IJp2pService<PipeAdvertisement> adService ) {
		super( source, pipeService );
		this.adService = adService;
	}
		
	/**
	 * Get an input pipe
	 * @return
	 * @throws IOException
	 */
	public InputPipe getInputPipe() throws IOException{
		return super.getModule().createInputPipe( pipead );
	}

	/**
	 * Get an input pipe
	 * @return
	 * @throws IOException
	 */
	public OutputPipe getOutputPipe() throws IOException{
		return super.getModule().createOutputPipe( pipead, (long) super.getPropertySource().getProperty( PipeServiceProperties.TIME_OUT ));
	}

	@Override
	public boolean start() {
		ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
		this.listener = new IComponentChangedListener(){

			@Override
			public void notifyServiceChanged(ComponentChangedEvent event) {
				if( event.getSource().equals( adService )){
					if( event.getChange().equals( AbstractServiceContainer.ServiceChange.COMPONENT_EVENT )){
						pipead = adService.getModule();
						if( pipead != null ){
							ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
							dispatcher.serviceChanged( new ComponentChangedEvent( this, ServiceChange.COMPONENT_EVENT));
						}
					}	
				}
			}

		};
		dispatcher.addServiceChangeListener(listener);;
		adService.start();
		return super.start();
	}

	@Override
	protected void deactivate() {
		ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
		if( this.listener != null )
			dispatcher.removeServiceChangeListener(listener);		
	}
	
	/**
	 * Get the discovery service
	 * @param adService
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Jp2pAdvertisementService<PipeAdvertisement> getAdvertisementService( ChaupalPipeService adService ){
		for( IJp2pComponent<?> component: adService.getChildren() ){
			if( component.getModule() instanceof Jp2pAdvertisementService )
				return (Jp2pAdvertisementService<PipeAdvertisement>) component.getModule();
		}
		return null;
	}
}