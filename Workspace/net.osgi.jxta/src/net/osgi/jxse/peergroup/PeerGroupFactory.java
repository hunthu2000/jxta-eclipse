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
package net.osgi.jxse.peergroup;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class PeerGroupFactory extends AbstractComponentFactory<PeerGroup> 
{
	private PeerGroup parentPeerGroup;
	
	public PeerGroupFactory( PeerGroupPropertySource source, PeerGroup parentPeerGroup ) {
		super( source );
		this.parentPeerGroup = parentPeerGroup;
	}
	
	@Override
	public boolean canCreate() {
		return ( this.parentPeerGroup != null );
	}

	@Override
	protected PeerGroup onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		return null;//parentPeerGroup.newGroup(null);
	}
}