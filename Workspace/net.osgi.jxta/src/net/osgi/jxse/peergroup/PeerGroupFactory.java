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
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.discovery.DiscoveryPropertySource;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.netpeergroup.NetPeerGroupPropertySource;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.AbstractPeerGroupProviderPropertySource.PeerGroupDirectives;
import net.osgi.jxse.utils.Utils;

public class PeerGroupFactory extends AbstractComponentFactory<PeerGroup> 
{
	private PeerGroup parentPeerGroup;
	
	public PeerGroupFactory( BuilderContainer container, IJxsePropertySource<IJxseProperties> parent) {
		super( container, parent );
	}

	@Override
	public String getComponentName() {
		return Components.PEERGROUP_SERVICE.toString();
	}
	
	@Override
	protected PeerGroupPropertySource onCreatePropertySource() {
		return new PeerGroupPropertySource( super.getParentSource());
	}

	@Override
	protected IJxseComponent<PeerGroup> onCreateComponent( IJxsePropertySource<IJxseProperties> properties) {
		return null;//parentPeerGroup.newGroup(null);
	}
	
	/**
	 * Extract the peergroup from the given factory, or return null if no peergroup is present 
	 * @param factory
	 * @return
	 */
	public static PeerGroup getPeerGroup( IComponentFactory<?> factory ){
		Object component = factory.getComponent();
		PeerGroup peergroup = null;
		if(  component instanceof PeerGroup )
			peergroup = (PeerGroup) component;
		else if( component instanceof IJxseComponent ){
			IJxseComponent<?> comp = (IJxseComponent<?>) component;
			peergroup = (PeerGroup) comp.getModule();		
		}
		return peergroup;
	}
	
	/**
	 * Returns true if the factory contasins the correct peergroup
	 * @param factory
	 * @return
	 */
	public static boolean isCorrectPeerGroup( IJxsePropertySource<?> current, IComponentFactory<?> factory ){
		IJxsePropertySource<?> ancestor = DiscoveryPropertySource.findPropertySource( current, PeerGroupDirectives.PEERGROUP );
		String peergroupName = null;
		if( ancestor != null ){
			peergroupName = ancestor.getDirective(PeerGroupDirectives.PEERGROUP);
		}
		if( Utils.isNull( peergroupName ))
			peergroupName = NetPeerGroupPropertySource.S_NET_PEER_GROUP;
		PeerGroup peergroup = PeerGroupFactory.getPeerGroup(factory);
		if( peergroup == null )
			return false;
		return ( peergroupName.toLowerCase().equals( peergroup.getPeerGroupName().toLowerCase()));	
	}
	
}