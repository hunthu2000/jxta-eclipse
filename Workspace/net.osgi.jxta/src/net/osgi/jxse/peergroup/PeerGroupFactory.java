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
import net.osgi.jxse.advertisement.AdvertisementPropertySource;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.peergroup.PeerGroupPropertySource.PeerGroupProperties;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;

public class PeerGroupFactory extends
		AbstractComponentFactory<PeerGroup, PeerGroupProperties, IJxseDirectives> 
	implements IPeerGroupProvider
{

	public static final String S_PEERGROUP_SERVICE = "PeerGroupService";

	private IPeerGroupProvider parentContainer;

	public PeerGroupFactory( IPeerGroupProvider parentContainer, PeerGroupPropertySource source ) {
		super( source );
		this.parentContainer = parentContainer;
	}

	@Override
	protected PeerGroup onCreateModule( IJxsePropertySource<PeerGroupProperties, IJxseDirectives> properties) {
		PeerGroup parent = parentContainer.getPeerGroup();
		return parent;//.newGroup( null);
	}
	
	protected AdvertisementPropertySource getAdvertisementPropertySource(){
		//for( IJxsePropertySource source: super. )
		return null;
	}
	
	@Override
	public PeerGroup getPeerGroup() {
		return super.getModule();
	}

	@Override
	public String getPeerGroupName() {
		return (String) super.getPropertySource().getProperty( PeerGroupProperties.ATTRIBUTE );
	}
}