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
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class ContextFactory extends AbstractComponentFactory<JxseServiceContext, IJxseProperties, IJxseDirectives>
	implements IPeerGroupProvider
{
	private IJxseServiceContext<NetworkManager, IJxseProperties, IJxseDirectives> context;
	
	public ContextFactory(JxseContextPropertySource source) {
		super(source );
	}
	
	@Override
	protected JxseServiceContext onCreateModule( IJxsePropertySource<IJxseProperties, IJxseDirectives> properties) {
		return new JxseServiceContext( super.getPropertySource() );
	}
	
	@Override
	public String getPeerGroupName() {
		return context.getModule().getNetPeerGroup().getPeerGroupName();
	}

	@Override
	public PeerGroup getPeerGroup() {
		return context.getModule().getNetPeerGroup();
	}
}
