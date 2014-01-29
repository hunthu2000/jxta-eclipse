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
package net.jp2p.jxta.discovery;

import net.jp2p.container.Jp2pContainerPropertySource;
import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.component.Jp2pComponent;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.jxta.discovery.DiscoveryPropertySource;
import net.jp2p.jxta.factory.AbstractPeerGroupDependencyFactory;
import net.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.jp2p.jxta.netpeergroup.NetPeerGroupFactory;
import net.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.jp2p.jxta.peergroup.PeerGroupPropertySource.PeerGroupDirectives;
import net.jxta.discovery.DiscoveryService;

public class DiscoveryServiceFactory extends
		AbstractPeerGroupDependencyFactory<DiscoveryService> {

	public DiscoveryServiceFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent) {
		super( container, parent );
	}

	@Override
	public String getComponentName() {
		return JxtaComponents.DISCOVERY_SERVICE.toString();
	}

	@Override
	protected DiscoveryPropertySource onCreatePropertySource() {
		return new DiscoveryPropertySource( super.getParentSource() );
	}

	@Override
	public void extendContainer() {
		
		//ALWAYS expect a peergroup, as the discovery service is not tied to a specific parent service
		Object peergroup = super.getPropertySource().getDirective( PeerGroupDirectives.PEERGROUP );
		if( peergroup == null )
			return;
		NetPeerGroupFactory factory = (NetPeerGroupFactory) this.getBuilder().getFactory( JxtaComponents.NET_PEERGROUP_SERVICE.toString() );
		if( factory != null )
			return;
		Jp2pContainerPropertySource root = (Jp2pContainerPropertySource) DiscoveryPropertySource.findRootPropertySource(this.getPropertySource() );
		IJp2pPropertySource<?> source = PeerGroupPropertySource.findPropertySource( root, JxtaComponents.NET_PEERGROUP_SERVICE.toString() );
		if( source == null ){
			factory =  new NetPeerGroupFactory( super.getBuilder(), root );
			super.getBuilder().addFactory( factory );
			factory.createPropertySource();
		}
		super.extendContainer();
	}

	@Override
	protected IJp2pComponent<DiscoveryService> onCreateComponent(
			IJp2pPropertySource<IJp2pProperties> properties) {
		return new Jp2pComponent<DiscoveryService>( super.getPropertySource(), super.getDependency().getModule().getDiscoveryService());
	}
}