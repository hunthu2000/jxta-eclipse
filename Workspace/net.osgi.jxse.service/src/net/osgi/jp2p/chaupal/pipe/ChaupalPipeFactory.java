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

import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.chaupal.advertisement.ChaupalAdvertisementFactory;
import net.osgi.jp2p.chaupal.advertisement.Jp2pAdvertisementService;
import net.osgi.jp2p.chaupal.discovery.ChaupalDiscoveryService;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.factory.AbstractFilterFactory;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.filter.FactoryFilter;
import net.osgi.jp2p.filter.FilterChain;
import net.osgi.jp2p.filter.FilterChain.Operators;
import net.osgi.jp2p.filter.FilterChainEvent;
import net.osgi.jp2p.filter.IComponentFactoryFilter;
import net.osgi.jp2p.filter.IFilterChainListener;
import net.osgi.jp2p.jxta.advertisement.service.AdvertisementServicePropertySource.AdvertisementDirectives;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.jxta.filter.PeerGroupFilter;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.WritePropertySourceWrapper;

public class ChaupalPipeFactory extends AbstractFilterFactory<PipeService>{

	private ChaupalAdvertisementFactory<PipeAdvertisement> adFactory;
	private PeerGroup peergroup;
	private IFilterChainListener listener;
	
	@SuppressWarnings("unchecked")
	public ChaupalPipeFactory( IContainerBuilder container, IComponentFactory<?> factory ) {
		super( container,  (IJp2pPropertySource<IJp2pProperties>) factory.getPropertySource().getParent() );
		super.setSource( factory.getPropertySource() );
		this.onCreatePropertySource();
		super.setCanCreate(false);
	}

	@Override
	protected IComponentFactoryFilter createFilter() {
		FilterChain<IJp2pComponent<PipeService>> chain = new FilterChain<IJp2pComponent<PipeService>>( Operators.SEQUENTIAL_AND, this );
		chain.addFilter( new PeerGroupFilter<IJp2pComponent<PipeService>>( this ));
		chain.addFilter( new FactoryFilter<IJp2pComponent<PipeService>, Jp2pAdvertisementService<?>>( BuilderEvents.COMPONENT_CREATED, JxtaComponents.PIPE_SERVICE.toString(), this ));
		listener = new IFilterChainListener(){

			@SuppressWarnings("unchecked")
			@Override
			public void notifyComponentCompleted(FilterChainEvent event) {
				if( event.getFilter() instanceof PeerGroupFilter ){
					PeerGroupFilter<?> filter = ( PeerGroupFilter<?>) event.getFilter();
					peergroup = filter.getPeergroup();
					return;
				}
				if( event.getFilter() instanceof FactoryFilter ){
					adFactory = (ChaupalAdvertisementFactory<PipeAdvertisement>) event.getFactory();
				}
			}
		};
		chain.addListener(listener);
		return chain;
	}

	@Override
	protected IJp2pPropertySource<IJp2pProperties> onCreatePropertySource() {
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) super.getPropertySource();
		source.setDirective( AdvertisementDirectives.TYPE, AdvertisementTypes.PIPE.toString());

		adFactory = new ChaupalAdvertisementFactory<PipeAdvertisement>( super.getBuilder(), source );
		IJp2pWritePropertySource<IJp2pProperties> child = new WritePropertySourceWrapper<IJp2pProperties>((IJp2pWritePropertySource<IJp2pProperties>) source, true ); 
		adFactory.setSource( child );
		super.getBuilder().addFactory( adFactory );
		return source;
	}
	
	@Override
	public void extendContainer() {
		adFactory.extendContainer();
		super.extendContainer();
	}

	//TODO change
	protected boolean isCorrectFactory(IComponentFactory<?> factory) {
		return ( factory.equals( adFactory ));
	}

	@Override
	protected ChaupalPipeService onCreateComponent( IJp2pPropertySource<IJp2pProperties> source) {
		PipeService pipes = peergroup.getPipeService();
		ChaupalPipeService service = new ChaupalPipeService( (IJp2pWritePropertySource<IJp2pProperties>) source, pipes, (Jp2pAdvertisementService<PipeAdvertisement>) adFactory.getComponent() );
		service.addChild( adFactory.getComponent() );
		ChaupalDiscoveryService ds = adFactory.getDiscoveryService();
		service.addChild( ds );
		return service;
	}
}