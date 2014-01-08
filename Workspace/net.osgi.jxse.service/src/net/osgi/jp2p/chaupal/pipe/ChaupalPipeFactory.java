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
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.chaupal.advertisement.ChaupalAdvertisementFactory;
import net.osgi.jp2p.chaupal.advertisement.Jp2pAdvertisementService;
import net.osgi.jp2p.chaupal.discovery.ChaupalDiscoveryService;
import net.osgi.jp2p.factory.AbstractDependencyFactory;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.jxta.peergroup.PeerGroupFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.WritePropertySourceWrapper;
import net.osgi.jp2p.utils.StringStyler;

public class ChaupalPipeFactory extends AbstractDependencyFactory<PipeService, Jp2pAdvertisementService<?>>{

	private ChaupalAdvertisementFactory<PipeAdvertisement> adFactory;
	private PeerGroup peergroup;
	
	@SuppressWarnings("unchecked")
	public ChaupalPipeFactory( ContainerBuilder container, IComponentFactory<?> factory ) {
		super( container,  (IJp2pPropertySource<IJp2pProperties>) factory.getPropertySource().getParent() );
		super.setSource( factory.getPropertySource() );
		this.onCreatePropertySource();
		super.setCanCreate(false);
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

	@Override
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

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
 		String name = StringStyler.styleToEnum(event.getFactory().getComponentName());
		if( !JxtaComponents.isComponent(name))
			return;
		IComponentFactory<?> factory = event.getFactory();
		switch( event.getBuilderEvent()){
		case COMPONENT_CREATED:
			if( !isComponentFactory( JxtaComponents.PIPE_SERVICE, factory ) || ( !event.getSource().equals( adFactory )) )
				break;
			boolean hasPeerGroup = (peergroup != null ) && ( adFactory.getComponent() != null );
			super.setCanCreate( hasPeerGroup );
			super.startComponent();
			break;
		case COMPONENT_STARTED:
			if(( !isComponentFactory( JxtaComponents.NET_PEERGROUP_SERVICE, event.getFactory() )) && 
					( !isComponentFactory( JxtaComponents.PEERGROUP_SERVICE, event.getFactory() )))
				break;
			if( !PeerGroupFactory.isCorrectPeerGroup( this.getPropertySource(), factory))
				return;
			peergroup = PeerGroupFactory.getPeerGroup( factory );
			break;
		default:
			break;
		}
		super.notifyChange(event);
	}
}