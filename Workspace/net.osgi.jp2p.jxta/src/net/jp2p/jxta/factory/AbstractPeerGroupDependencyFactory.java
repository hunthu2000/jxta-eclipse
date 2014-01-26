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
package net.jp2p.jxta.factory;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.factory.AbstractComponentDependencyFactory;
import net.jp2p.container.factory.filter.IComponentFactoryFilter;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.jxta.filter.PeerGroupFilter;
import net.jxta.peergroup.PeerGroup;

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
		return (PeerGroup) super.getDependency().getModule();
	}
}
