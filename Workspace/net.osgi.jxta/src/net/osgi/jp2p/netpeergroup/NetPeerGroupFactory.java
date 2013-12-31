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
package net.osgi.jp2p.netpeergroup;

import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.context.ContainerFactory;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.utils.StringStyler;

public class NetPeerGroupFactory extends AbstractComponentFactory<PeerGroup>{

	private NetworkManager manager;

	public NetPeerGroupFactory( ContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent ) {
		super( container, parent, false );
	}

	@Override
	public String getComponentName() {
		return Components.NET_PEERGROUP_SERVICE.toString();
	}	
	
	@Override
	public NetPeerGroupPropertySource onCreatePropertySource() {
		NetPeerGroupPropertySource source = new NetPeerGroupPropertySource( super.getParentSource());
		return source;
	}
	
	@Override
	protected IJp2pComponent<PeerGroup> onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		NetPeerGroupService service = new NetPeerGroupService( this, manager );
		super.setCompleted( true );
		return service;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		String name = StringStyler.styleToEnum(event.getFactory().getComponentName());
		if( !Components.isComponent(name))
			return;

		switch( event.getBuilderEvent()){
		case COMPONENT_CREATED:
			switch(Components.valueOf( name )){

			case NETWORK_MANAGER:
				if( NetPeerGroupPropertySource.isAutoStart(super.getPropertySource() )){
					this.manager = ((IJp2pComponent<NetworkManager>) event.getFactory().getComponent()).getModule();
					super.setCanCreate(this.manager != null );
				}
				break;
			default:
				break;
			}
			break;
		case FACTORY_COMPLETED:
			if( !isComponentFactory( Components.JXSE_CONTEXT, event.getFactory() ))
				return;
			IComponentFactory<?> factory = event.getFactory();
			ContainerFactory cf = (ContainerFactory) factory;
			if( cf.isAutoStart() )
				this.startComponent();
			break;
		default:
			break;
		}
	}
}