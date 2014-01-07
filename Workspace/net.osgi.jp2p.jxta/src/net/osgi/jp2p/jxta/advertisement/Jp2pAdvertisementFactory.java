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
package net.osgi.jp2p.jxta.advertisement;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.jxta.discovery.DiscoveryServiceFactory;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.factory.AbstractDependencyFactory;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.jxta.peergroup.PeerGroupFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public class Jp2pAdvertisementFactory<T extends Advertisement> extends AbstractDependencyFactory<T, IJp2pComponent<DiscoveryService>> {

	public Jp2pAdvertisementFactory( ContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parentSource) {
		super( container, parentSource );
	}

	@Override
	protected AdvertisementPropertySource onCreatePropertySource() {
		AdvertisementPropertySource source = new AdvertisementPropertySource( JxtaComponents.ADVERTISEMENT_SERVICE.toString(), super.getParentSource() );
		return source;
	}
	
	@Override
	protected boolean isCorrectFactory(IComponentFactory<?> factory) {
		if( !(factory instanceof DiscoveryServiceFactory  ))
			return false;
		String peergroup = PeerGroupFactory.findAncestorPeerGroup(this.getPropertySource() );
		String fpg = PeerGroupFactory.findAncestorPeerGroup(factory.getPropertySource() );
		return peergroup.equals(fpg);
	}

	/**
	 * Make public in order to activate this externally by other factories
	 */
	@Override
	public IJp2pComponent<T> createComponent() {
		return super.createComponent();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected IJp2pComponent<T> onCreateComponent( IJp2pPropertySource<IJp2pProperties> source) {
		AdvertisementPropertySource adsource = (AdvertisementPropertySource) source.getChild( JxtaComponents.ADVERTISEMENT.toString() );
		JxtaAdvertisementFactory factory = (JxtaAdvertisementFactory) super.getBuilder().getFactory( adsource );
		return (IJp2pComponent<T>) factory.createComponent();
	}
}
