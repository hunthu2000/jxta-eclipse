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
package net.osgi.jxse.pipe;

import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeService;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.JxseComponentNode;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.PeerGroupFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;

public class PipeServiceFactory extends
		AbstractComponentFactory<PipeService> {

	public static final String S_DISCOVERY_SERVICE = "DiscoveryService";
	
	private PeerGroup peergroup;

	public PipeServiceFactory( BuilderContainer container, IJxsePropertySource<IJxseProperties> parent ) {
		super( container, parent );
	}

	@Override
	public String getComponentName() {
		return Components.PIPE_SERVICE.toString();
	}

	@Override
	protected PipePropertySource onCreatePropertySource() {
		return new PipePropertySource( super.getParentSource() );
	}

	@Override
	protected IJxseComponent<PipeService> onCreateComponent( IJxsePropertySource<IJxseProperties> properties) {
		return new JxseComponentNode<PipeService>( peergroup.getPipeService());
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