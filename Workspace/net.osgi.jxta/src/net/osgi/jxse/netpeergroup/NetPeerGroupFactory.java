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
package net.osgi.jxse.netpeergroup;

import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.context.ContextFactory;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;

public class NetPeerGroupFactory extends AbstractComponentFactory<PeerGroup>{

	private NetworkManager manager;

	public NetPeerGroupFactory( BuilderContainer container, IJxsePropertySource<IJxseProperties> parent ) {
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
	protected IJxseComponent<PeerGroup> onCreateComponent( IJxsePropertySource<IJxseProperties> properties) {
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
					this.manager = ((IJxseComponent<NetworkManager>) event.getFactory().getComponent()).getModule();
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
			ContextFactory cf = (ContextFactory) factory;
			if( cf.isAutoStart() )
				this.startComponent();
			break;
		default:
			break;
		}
	}
}