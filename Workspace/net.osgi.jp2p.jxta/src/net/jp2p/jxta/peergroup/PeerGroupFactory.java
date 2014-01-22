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
package net.jp2p.jxta.peergroup;

import java.net.URI;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.component.Jp2pComponent;
import net.jp2p.container.factory.IComponentFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.utils.Utils;
import net.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.jp2p.jxta.advertisement.ModuleClassAdvertisementPropertySource;
import net.jp2p.jxta.advertisement.ModuleImplAdvertisementPropertySource;
import net.jp2p.jxta.advertisement.ModuleSpecAdvertisementPropertySource;
import net.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.jp2p.jxta.discovery.DiscoveryPropertySource;
import net.jp2p.jxta.factory.AbstractPeerGroupDependencyFactory;
import net.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.jp2p.jxta.peergroup.PeerGroupFactory;
import net.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.jp2p.jxta.peergroup.PeerGroupPropertySource.PeerGroupDirectives;
import net.jp2p.jxta.peergroup.PeerGroupPropertySource.PeerGroupProperties;
import net.jp2p.jxta.pipe.PipeAdvertisementPropertySource;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

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

	/**
	 * Create a peergroup from an implementation advertisement
	 * @param source
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public PeerGroup createPeerGroupFromModuleImpl( IJp2pPropertySource<IJp2pProperties> source ) throws Exception{
		ModuleImplAdvertisement miad = ModuleImplAdvertisementPropertySource.createModuleImplAdvertisement(null, super.getPeerGroup() );
		PeerGroupID id = null;
		String name = null;
		String description=  null;
		if( source != null ){
			super.getPeerGroup().getDiscoveryService().publish(miad);
			id = (PeerGroupID)IDFactory.fromURI( (URI) source.getProperty( PeerGroupProperties.GROUP_ID ));
			name = (String) source.getProperty( PeerGroupProperties.NAME ); 
			description = (String) source.getProperty( PeerGroupProperties.DESCRIPTION );
		}
		PeerGroup peergroup = super.getPeerGroup().newGroup( id, miad, name, description);
		return peergroup;
	}

	/**
	 * Create a peergroup from an implementation advertisement
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public PeerGroup createPeerGroupFromPeerAds( ModuleSpecAdvertisementPropertySource msps, ModuleClassAdvertisementPropertySource mcps, PipeAdvertisementPropertySource paps, PeerGroupAdvertisementPropertySource pgps ) throws Exception{
		ModuleClassAdvertisement mcad = ModuleClassAdvertisementPropertySource.createModuleClassAdvertisement(mcps );
		super.getPeerGroup().getDiscoveryService().publish( mcad );
		PipeAdvertisement pipeAdv = PipeAdvertisementPropertySource.createPipeAdvertisement(paps, super.getPeerGroup() );
		ModuleSpecAdvertisement msad = ModuleSpecAdvertisementPropertySource.createModuleSpecAdvertisement(msps, mcad, pipeAdv);
		super.getPeerGroup().getDiscoveryService().publish( msad );
		PeerGroupAdvertisement pgad = PeerGroupAdvertisementPropertySource.createPeerGroupAdvertisement( pgps, msad);
		PeerGroup peergroup = super.getPeerGroup().newGroup( pgad );
		return peergroup;
	}

	@Override
	protected IJp2pComponent<PeerGroup> onCreateComponent( IJp2pPropertySource<IJp2pProperties> source ) {
		PeerGroupAdvertisementPropertySource pgps = (PeerGroupAdvertisementPropertySource) AdvertisementPropertySource.findAdvertisementDescendant(source, AdvertisementTypes.PEERGROUP );
		ModuleSpecAdvertisementPropertySource msps = (ModuleSpecAdvertisementPropertySource) AdvertisementPropertySource.findAdvertisementDescendant(source, AdvertisementTypes.MODULE_SPEC );
		ModuleClassAdvertisementPropertySource mcps = (ModuleClassAdvertisementPropertySource) AdvertisementPropertySource.findAdvertisementDescendant(msps, AdvertisementTypes.MODULE_CLASS );
		PipeAdvertisementPropertySource paps = (PipeAdvertisementPropertySource) AdvertisementPropertySource.findAdvertisementDescendant(pgps, AdvertisementTypes.PIPE );

		String name = (String) super.getPropertySource().getProperty( PeerGroupProperties.NAME );
		String description = (String) super.getPropertySource().getProperty( PeerGroupProperties.DESCRIPTION );
		boolean publish = PeerGroupPropertySource.getBoolean(super.getPropertySource(), PeerGroupDirectives.PUBLISH );

		IJp2pComponent<PeerGroup> component = null;
		try {
			PeerGroup peergroup = this.createPeerGroupFromModuleImpl( super.getPropertySource() );
			PeerGroupAdvertisement pgadv = peergroup.getPeerGroupAdvertisement();
			IJp2pWritePropertySource<IJp2pProperties> ws = (IJp2pWritePropertySource<IJp2pProperties>) super.getPropertySource();
			component = new Jp2pComponent<PeerGroup>( super.getPropertySource(), peergroup );
			if( publish ){
				peergroup.publishGroup(name, description);
				return component;
			}
		} catch ( Exception e) {
			e.printStackTrace();
		}
		return component;
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
