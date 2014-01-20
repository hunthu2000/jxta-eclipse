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
package net.osgi.jp2p.chaupal.jxta.peergroup;

import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.chaupal.jxta.advertisement.ChaupalAdvertisementFactory;
import net.osgi.jp2p.chaupal.jxta.advertisement.Jp2pAdvertisementService;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.ModuleClassAdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.ModuleSpecAdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.jxta.peergroup.PeerGroupAdvertisementPropertySource;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.osgi.jp2p.jxta.pipe.PipeAdvertisementPropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public class ChaupalPeerGroupFactory extends ChaupalAdvertisementFactory<PeerGroup, PeerGroupAdvertisement>{

	public ChaupalPeerGroupFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parentSource ) {
		super( container,  AdvertisementTypes.PEERGROUP, parentSource );
	}

	@Override
	protected AdvertisementPropertySource onCreatePropertySource() {
		AdvertisementPropertySource source = new PeerGroupPropertySource( super.getParentSource() );
		source.setDirective( AdvertisementDirectives.TYPE, AdvertisementTypes.PEERGROUP.toString());
		return source;
	}
	
	/**
	 * Create a peergroup from an implementation advertisement
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public PeerGroupAdvertisement createPeerGroupAdsFromPeerAds( ModuleSpecAdvertisementPropertySource msps, ModuleClassAdvertisementPropertySource mcps, PeerGroupPropertySource paps, PeerGroupAdvertisementPropertySource pgps ) throws Exception{
		ModuleClassAdvertisement mcad = ModuleClassAdvertisementPropertySource.createModuleClassAdvertisement(mcps );
		super.getPeerGroup().getDiscoveryService().publish( mcad );
		PipeAdvertisement pipeAdv = PipeAdvertisementPropertySource.createPipeAdvertisement(paps, super.getPeerGroup() );
		ModuleSpecAdvertisement msad = ModuleSpecAdvertisementPropertySource.createModuleSpecAdvertisement(msps, mcad, pipeAdv);
		super.getPeerGroup().getDiscoveryService().publish( msad );
		return PeerGroupAdvertisementPropertySource.createPeerGroupAdvertisement( pgps, msad);
	}

	@Override
	protected PeerGroupAdvertisement createAdvertisement( IJp2pPropertySource<IJp2pProperties> source) {
		PeerGroupAdvertisementPropertySource pgps = (PeerGroupAdvertisementPropertySource) AdvertisementPropertySource.findAdvertisementDescendant(source, AdvertisementTypes.PEERGROUP );
		ModuleSpecAdvertisementPropertySource msps = (ModuleSpecAdvertisementPropertySource) AdvertisementPropertySource.findAdvertisementDescendant(source, AdvertisementTypes.MODULE_SPEC );
		ModuleClassAdvertisementPropertySource mcps = (ModuleClassAdvertisementPropertySource) AdvertisementPropertySource.findAdvertisementDescendant(msps, AdvertisementTypes.MODULE_CLASS );
		try {
			return createPeerGroupAdsFromPeerAds(msps, mcps, (PeerGroupPropertySource) super.getPropertySource(), pgps);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected IJp2pComponent<PeerGroup> createComponent( PeerGroupAdvertisement advertisement) {
		return new PeerGroupService( (PeerGroupPropertySource) super.getPropertySource(), advertisement, super.getPeerGroup(), super.getDiscoveryService() );
	}
}