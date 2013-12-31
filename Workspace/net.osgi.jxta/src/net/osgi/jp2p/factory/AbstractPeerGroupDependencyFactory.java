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
package net.osgi.jp2p.factory;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.peergroup.PeerGroupFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.utils.StringStyler;

public abstract class AbstractPeerGroupDependencyFactory<T extends Object> extends
		AbstractComponentFactory<T> {

	private PeerGroup peergroup;

	public AbstractPeerGroupDependencyFactory( ContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent ) {
		super( container, parent );
	}

	/**
	 * Get the dependency that must be provided in order to allow creation of the cpomponent
	 * @return
	 */
	protected PeerGroup getPeerGroup() {
		return peergroup;
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		super.notifyChange(event);
		String name = StringStyler.styleToEnum(event.getFactory().getComponentName());
		if( !Components.isComponent(name))
			return;

		switch( event.getBuilderEvent()){
		case COMPONENT_STARTED:
			if( !isComponentFactory( Components.PEERGROUP_SERVICE, event.getFactory() ) && 
					!isComponentFactory( Components.NET_PEERGROUP_SERVICE, event.getFactory() ))
				return;
			IComponentFactory<?> factory = event.getFactory();
			if( !PeerGroupFactory.isCorrectPeerGroup( this.getPropertySource(), factory))
				return;
			peergroup = PeerGroupFactory.getPeerGroup( factory );
			super.setCanCreate( peergroup != null );
			super.startComponent();
			break;
		default:
			break;
		}
	}

}