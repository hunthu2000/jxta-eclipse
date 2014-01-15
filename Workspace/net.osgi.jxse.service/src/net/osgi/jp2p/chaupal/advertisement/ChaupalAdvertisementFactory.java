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
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.chaupal.discovery.ChaupalDiscoveryService;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.filter.AbstractComponentFactoryFilter;
import net.osgi.jp2p.filter.FilterChain;
import net.osgi.jp2p.filter.IComponentFactoryFilter;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.service.AdvertisementServicePropertySource.AdvertisementServiceProperties;
import net.osgi.jp2p.jxta.advertisement.service.Jp2pAdvertisementFactory;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.jxta.factory.JxtaFactoryUtils;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.utils.Utils;

public class ChaupalAdvertisementFactory<T extends Advertisement> extends Jp2pAdvertisementFactory<T>{
	
	private ChaupalDiscoveryService service;
	
	public ChaupalAdvertisementFactory( IContainerBuilder builder, IJp2pPropertySource<IJp2pProperties> parent ) {
		super( builder, (IJp2pPropertySource<IJp2pProperties>) parent );
	}

	@SuppressWarnings("unchecked")
	public ChaupalAdvertisementFactory( IContainerBuilder builder, IComponentFactory<Advertisement> factory ) {
		super( builder, (IJp2pPropertySource<IJp2pProperties>) factory.getPropertySource().getParent() );
	}

	
	@SuppressWarnings("unchecked")
	protected IComponentFactoryFilter createFilter(){
		FilterChain<T> bf = (FilterChain<T>) super.createFilter();
		IComponentFactoryFilter filter = new AbstractComponentFactoryFilter<T>((IComponentFactory<T>) this){

			@Override
			public boolean onAccept(ComponentBuilderEvent<?> event) {
				switch( event.getBuilderEvent() ){
				case COMPONENT_CREATED:
					if( event.getFactory().getComponent() instanceof ChaupalDiscoveryService){
						service = (ChaupalDiscoveryService) event.getFactory().getComponent();
						return true;
					}
					return false;
				default:
					return false;
				}
			}
			
		};
		bf.addFilter(filter);
		return bf;
	}
	/**
	 * Get the used discovery service
	 * @return
	 */
	public ChaupalDiscoveryService getDiscoveryService(){
		return service;
	}
	
	/**
	 * We allow others to override the source
	 */
	@Override
	public void setSource(IJp2pPropertySource<IJp2pProperties> source) {
		super.setSource(source);
	}

	/**
	 * Returns true if the property source is a child of the parent. 
	 * @param source
	 * @return
	 */
	@Override
	protected boolean isChild( IJp2pPropertySource<?> source ){
		if(  AdvertisementPropertySource.isChild( this.getPropertySource(), source ))
			return true;
		return AdvertisementPropertySource.isChild( this.getPropertySource().getParent(), source );
		
	}

	@Override
	public void extendContainer() {
		IContainerBuilder builder = super.getBuilder();
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
		String name = (String) source.getProperty( AdvertisementServiceProperties.NAME );
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
		Jp2pAdvertisementService jadservice = new Jp2pAdvertisementService( (IJp2pWritePropertySource<IJp2pProperties>) source, super.getAdvertisment(), service );
		return jadservice;
	}
}
