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
package net.osgi.jp2p.jxta.factory;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.factory.AbstractComponentDependencyFactory;
import net.osgi.jp2p.filter.IComponentFactoryFilter;
import net.osgi.jp2p.jxta.filter.PeerGroupFilter;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public abstract class AbstractPeerGroupDependencyFactory<T extends Object> extends
		AbstractComponentDependencyFactory<T, IJp2pComponent<PeerGroup>> {

	public AbstractPeerGroupDependencyFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent ) {
		super( container, parent );
	}

	@Override
	protected IComponentFactoryFilter createFilter() {
		return new PeerGroupFilter<IJp2pComponent<T>>( this );
	}
	
	protected PeerGroup getPeerGroup(){
		return (PeerGroup) super.getDependency();
	}
}
