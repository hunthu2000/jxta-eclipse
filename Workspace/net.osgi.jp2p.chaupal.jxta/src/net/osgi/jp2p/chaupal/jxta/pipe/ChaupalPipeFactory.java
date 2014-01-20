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

import java.net.URISyntaxException;

import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jp2p.activator.IJp2pService;
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.chaupal.jxta.advertisement.ChaupalAdvertisementFactory;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.jxta.pipe.PipeAdvertisementPropertySource;
import net.osgi.jp2p.jxta.pipe.PipePropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class ChaupalPipeFactory extends ChaupalAdvertisementFactory<PipeService, PipeAdvertisement>{

	public ChaupalPipeFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parentSource ) {
		super( container,  AdvertisementTypes.PIPE, parentSource );
	}

	@Override
	protected AdvertisementPropertySource onCreatePropertySource() {
		AdvertisementPropertySource source = new PipePropertySource( super.getParentSource() );
		source.setDirective( AdvertisementDirectives.TYPE, AdvertisementTypes.PIPE.toString());
		return source;
	}
	

	@Override
	protected PipeAdvertisement createAdvertisement( IJp2pPropertySource<IJp2pProperties> source) {
		PipeAdvertisement pipeAd = null;
		try {
			pipeAd = PipeAdvertisementPropertySource.createPipeAdvertisement(source, super.getPeerGroup());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return pipeAd;
	}

	@Override
	protected IJp2pComponent<PipeService> createComponent(
			PipeAdvertisement advertisement) {
		PipeService pipes = super.getPeerGroup().getPipeService();
		IJp2pService<PipeAdvertisement> adService = super.getAdvertisementService();
		ChaupalPipeService service = new ChaupalPipeService( (IJp2pWritePropertySource<IJp2pProperties>) super.getPropertySource(), pipes, adService );
		return service;
	}
}