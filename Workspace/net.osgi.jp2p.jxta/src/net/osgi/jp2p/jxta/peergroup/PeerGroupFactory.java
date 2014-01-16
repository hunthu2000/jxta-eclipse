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
package net.osgi.jp2p.jxta.peergroup;

import java.net.URISyntaxException;

import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.jxta.advertisement.ModuleClassAdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.ModuleImplAdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.ModuleSpecAdvertisementPropertySource;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource;
import net.osgi.jp2p.jxta.factory.AbstractPeerGroupDependencyFactory;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.utils.Utils;
import net.osgi.jp2p.jxta.peergroup.PeerGroupFactory;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource.PeerGroupDirectives;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource.PeerGroupProperties;
import net.osgi.jp2p.jxta.pipe.PipeAdvertisementPropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class PeerGroupFactory extends AbstractPeerGroupDependencyFactory<PeerGroup> 
{
	public PeerGroupFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent) {
		super( container, parent );
	}

	@Override
	public String getComponentName() {
		return JxtaComponents.PEERGROUP_SERVICE.toString();
	}
	
	@Override
	protected PeerGroupPropertySource onCreatePropertySource() {
		return new PeerGroupPropertySource( super.getParentSource());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected IJp2pComponent<PeerGroup> onCreateComponent( IJp2pPropertySource<IJp2pProperties> source ) {
		ModuleImplAdvertisementPropertySource mips = (ModuleImplAdvertisementPropertySource) AdvertisementPropertySource.findAdvertisementDescendant(source, AdvertisementTypes.MODULE_IMPL );
		ModuleSpecAdvertisementPropertySource msps = (ModuleSpecAdvertisementPropertySource) AdvertisementPropertySource.findAdvertisementDescendant(source, AdvertisementTypes.MODULE_SPEC );
		ModuleClassAdvertisementPropertySource mcps = (ModuleClassAdvertisementPropertySource) AdvertisementPropertySource.findAdvertisementDescendant(msps, AdvertisementTypes.MODULE_CLASS );
		PipeAdvertisementPropertySource paps = (PipeAdvertisementPropertySource) AdvertisementPropertySource.findAdvertisementDescendant(mips, AdvertisementTypes.PIPE );
		
		ModuleImplAdvertisement miad = null;
		try {
			miad = ModuleImplAdvertisementPropertySource.createModuleClassAdvertisement(mips );
			ModuleClassAdvertisement mcad = ModuleClassAdvertisementPropertySource.createModuleClassAdvertisement(mcps );
			PipeAdvertisement pipeAdv = PipeAdvertisementPropertySource.createPipeAdvertisement(paps, super.getPeerGroup() );
			ModuleSpecAdvertisement msad = ModuleSpecAdvertisementPropertySource.createModuleSpecAdvertisement(msps, mcad, pipeAdv);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		String name = (String) super.getPropertySource().getProperty( PeerGroupProperties.PEERGROUP_NAME );
		String description = (String) super.getPropertySource().getProperty( PeerGroupProperties.DESCRIPTION );
		boolean publish = PeerGroupPropertySource.getBoolean(super.getPropertySource(), PeerGroupDirectives.PUBLISH );
		PeerGroupPreferences prefs = new PeerGroupPreferences((IJp2pWritePropertySource<IJp2pProperties>) super.getPropertySource());
		try {
			return (IJp2pComponent<PeerGroup>) super.getPeerGroup().newGroup( prefs.getPeerGroupID(), miad, name, description, publish);
		} catch (PeerGroupException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
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
		else if( component instanceof IJp2pComponent ){
			IJp2pComponent<?> comp = (IJp2pComponent<?>) component;
			if( !( comp.getModule() instanceof PeerGroup ))
				return null;
			peergroup = (PeerGroup) comp.getModule();		
		}
		return peergroup;
	}
	
	/**
	 * Returns true if the factory contains the correct peergroup
	 * @param factory
	 * @return
	 */
	public static boolean isCorrectPeerGroup( IJp2pPropertySource<?> current, IComponentFactory<?> factory ){
		if( !isComponentFactory( JxtaComponents.PEERGROUP_SERVICE, factory ) && 
				!isComponentFactory( JxtaComponents.NET_PEERGROUP_SERVICE, factory ))
			return false;
		IJp2pPropertySource<?> ancestor = DiscoveryPropertySource.findPropertySource( current, PeerGroupDirectives.PEERGROUP );
		String peergroupName = null;
		if( ancestor != null ){
			peergroupName = ancestor.getDirective(PeerGroupDirectives.PEERGROUP);
		}
		if( Utils.isNull( peergroupName ))
			peergroupName = PeerGroupPropertySource.S_NET_PEER_GROUP;
		PeerGroup peergroup = PeerGroupFactory.getPeerGroup(factory);
		if( peergroup == null )
			return false;
		return ( peergroupName.toLowerCase().equals( peergroup.getPeerGroupName().toLowerCase()));	
	}

	/**
	 * Return the first peergroup along the ancestors, or the netpeertgroup if none were found
	 * @param current
	 * @param factory
	 * @return
	 */
	public static String findAncestorPeerGroup( IJp2pPropertySource<?> current ){
		String peergroup = PeerGroupPropertySource.findFirstAncestorDirective(current, PeerGroupDirectives.PEERGROUP );
		if( Utils.isNull( peergroup ))
			return PeerGroupPropertySource.S_NET_PEER_GROUP;
		else
			return peergroup;
	}
}
