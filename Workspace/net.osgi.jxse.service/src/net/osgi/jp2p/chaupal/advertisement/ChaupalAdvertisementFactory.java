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
package net.osgi.jp2p.chaupal.advertisement;

import net.jxta.document.Advertisement;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.chaupal.discovery.ChaupalDiscoveryService;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementProperties;
import net.osgi.jp2p.jxta.advertisement.Jp2pAdvertisementFactory;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.jxta.factory.JxtaFactoryUtils;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.utils.Utils;

public class ChaupalAdvertisementFactory<T extends Advertisement> extends Jp2pAdvertisementFactory<T>{

	public ChaupalAdvertisementFactory( ContainerBuilder builder, IJp2pPropertySource<IJp2pProperties> parent ) {
		super( builder, (IJp2pPropertySource<IJp2pProperties>) parent );
	}

	@SuppressWarnings("unchecked")
	public ChaupalAdvertisementFactory( ContainerBuilder builder, IComponentFactory<Advertisement> factory ) {
		super( builder, (IJp2pPropertySource<IJp2pProperties>) factory.getPropertySource().getParent() );
	}

	/**
	 * Get the used discovery service
	 * @return
	 */
	public ChaupalDiscoveryService getDiscoveryService(){
		return (ChaupalDiscoveryService) super.getDependency();
	}
	
	/**
	 * We allow others to override the source
	 */
	@Override
	public void setSource(IJp2pPropertySource<IJp2pProperties> source) {
		super.setSource(source);
	}

	@Override
	public void extendContainer() {
		ContainerBuilder builder = super.getBuilder();
		IComponentFactory<?> df = builder.getFactory(JxtaComponents.DISCOVERY_SERVICE.toString());
		if( df == null ){
			df = JxtaFactoryUtils.getDefaultFactory(builder, super.getPropertySource(), JxtaComponents.DISCOVERY_SERVICE.toString());
			df.createPropertySource();
			builder.addFactory( df ); 
		}
		DiscoveryPropertySource ds = (DiscoveryPropertySource) df.getPropertySource();

		AdvertisementPropertySource source = (AdvertisementPropertySource) super.getPropertySource().getChild( JxtaComponents.ADVERTISEMENT.toString() );
		if( source == null )
			return;
		Object value = ds.getProperty( DiscoveryProperties.ATTRIBUTE );
		if( value == null ){
			ds.setProperty(DiscoveryProperties.ATTRIBUTE, DiscoveryPropertySource.S_NAME );
		}
		String name = (String) source.getProperty( AdvertisementProperties.NAME );
		if( Utils.isNull( name ))
			name = DiscoveryPropertySource.S_WILDCARD;
		value = ds.getProperty( DiscoveryProperties.WILDCARD );
		if( value == null ){
			ds.setProperty(DiscoveryProperties.WILDCARD, name );
		}
		super.extendContainer();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Jp2pAdvertisementService onCreateComponent( IJp2pPropertySource<IJp2pProperties> source) {
		IJp2pComponent<T> adservice = super.onCreateComponent( source );
		Jp2pAdvertisementService service = new Jp2pAdvertisementService( (IJp2pWritePropertySource<IJp2pProperties>) source, adservice.getModule(), (ChaupalDiscoveryService) super.getDependency() );
		return service;
	}
}