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
package net.osgi.jxse.discovery;

import net.jxta.discovery.DiscoveryService;
import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.JxseComponent;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.peergroup.PeerGroupFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;

public class DiscoveryServiceFactory extends
		AbstractComponentFactory<DiscoveryService> {

	private PeerGroup peergroup;
	
	public DiscoveryServiceFactory( BuilderContainer container, IJxsePropertySource<IJxseProperties> parent) {
		super( container, parent );
	}

	@Override
	public String getComponentName() {
		return Components.DISCOVERY_SERVICE.toString();
	}

	@Override
	protected DiscoveryPropertySource onCreatePropertySource() {
		return new DiscoveryPropertySource( super.getParentSource() );
	}

	@Override
	protected IJxseComponent<DiscoveryService> onCreateComponent(
			IJxsePropertySource<IJxseProperties> properties) {
		return new JxseComponent<DiscoveryService>( peergroup.getDiscoveryService());
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