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
package net.osgi.jxse.context;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.netpeergroup.NetPeerGroupPropertySource;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class ContextFactory extends AbstractComponentFactory<JxseServiceContext, IJxseProperties>
	implements IPeerGroupProvider
{
	private JxseServiceContext context;
	
	public ContextFactory(JxseContextPropertySource source) {
		super(source );
		this.extendPropertySource( source );
	}

	protected void extendPropertySource( JxseContextPropertySource propertySource ){
		IJxsePropertySource<?> source = propertySource.getChild( Components.NET_PEERGROUP_SERVICE.toString() );
		if( source != null )
			return;
		if( JxseContextPropertySource.isAutoStart( propertySource )){
			propertySource.addChild( new NetPeerGroupPropertySource( propertySource ));
		}
	}
	

	@Override
	protected JxseServiceContext onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		this.context = new JxseServiceContext( super.getPropertySource() );
		return context;
	}
	
	@Override
	public String getPeerGroupName() {
		return context.getModule().getNetPeerGroup().getPeerGroupName();
	}

	@Override
	public PeerGroup getPeerGroup() {
		if( context.getModule() == null )
			return null;
		return context.getModule().getNetPeerGroup();
	}
}
