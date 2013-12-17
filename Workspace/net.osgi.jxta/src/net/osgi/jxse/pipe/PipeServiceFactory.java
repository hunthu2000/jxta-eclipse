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

import net.jxta.pipe.PipeService;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class PipeServiceFactory extends
		AbstractComponentFactory<PipeService, IJxseProperties> {

	public static final String S_DISCOVERY_SERVICE = "DiscoveryService";

	private IPeerGroupProvider peerGroupContainer;

	public PipeServiceFactory( IPeerGroupProvider peerGroupContainer, PipePropertySource source ) {
		super( source );
		this.peerGroupContainer = peerGroupContainer;
	}

	@Override
	protected PipeService onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		if( peerGroupContainer.getPeerGroup() == null ){
			super.setCompleted(false );
			return null;
		}
			
		return peerGroupContainer.getPeerGroup().getPipeService();
	}
}