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
package net.osgi.jp2p.chaupal.jxta.advertisement;

import net.jxta.document.Advertisement;
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.chaupal.jxta.IChaupalComponents.ChaupalComponents;
import net.osgi.jp2p.chaupal.jxta.advertisement.AdvertisementServicePropertySource.AdvertisementServiceProperties;
import net.osgi.jp2p.chaupal.jxta.discovery.ChaupalDiscoveryService;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.filter.AbstractComponentFactoryFilter;
import net.osgi.jp2p.filter.FilterChain;
import net.osgi.jp2p.filter.IComponentFactoryFilter;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.jxta.advertisement.service.AbstractJxtaAdvertisementFactory;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource;
import net.osgi.jp2p.jxta.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.osgi.jp2p.jxta.factory.JxtaFactoryUtils;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.utils.Utils;

public abstract class ChaupalAdvertisementFactory<T extends Object, U extends Advertisement> extends AbstractJxtaAdvertisementFactory<T,U>{
	
	private ChaupalDiscoveryService service;
	private Jp2pAdvertisementService<U> jas;
	private U advertisement;
	
	public ChaupalAdvertisementFactory( IContainerBuilder builder, AdvertisementTypes type, IJp2pPropertySource<IJp2pProperties> parent ) {
		super( builder, type, parent );
	}

	@Override
	protected AdvertisementPropertySource onCreatePropertySource() {
		AdvertisementPropertySource source = (AdvertisementPropertySource) super.onCreatePropertySource();
		source.setDirective( Directives.BLOCK_CREATION, Boolean.FALSE.toString());
		return source;
	}

	@Override
	public void extendContainer() {
		IContainerBuilder builder = super.getBuilder();
		IComponentFactory<?> df = builder.getFactory(ChaupalComponents.DISCOVERY_SERVICE.toString());
		if( df == null ){
			df = JxtaFactoryUtils.getDefaultFactory(builder, new String[0], super.getPropertySource(), ChaupalComponents.DISCOVERY_SERVICE.toString());
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

	

	@SuppressWarnings("unchecked")
	protected IComponentFactoryFilter createFilter(){
		FilterChain<U> bf = (FilterChain<U>) super.createFilter();
		IComponentFactoryFilter filter = new AbstractComponentFactoryFilter<U>((IComponentFactory<U>) this){

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
	 * Get the advertisement
	 * @return
	 */
	protected U getAdvertisement() {
		return advertisement;
	}

	/**
	 * Get the advertisement service
	 * @return
	 */
	protected Jp2pAdvertisementService<U> getAdvertisementService() {
		return jas;
	}

	/**
	 * Returns true if the property source is a child of the parent. 
	 * @param source
	 * @return
	 */
	protected boolean isChild( IJp2pPropertySource<?> source ){
		if(  AdvertisementPropertySource.isChild( this.getPropertySource(), source ))
			return true;
		return AdvertisementPropertySource.isChild( this.getPropertySource().getParent(), source );
	}

	@Override
	protected IJp2pComponent<T> onCreateComponent( IJp2pPropertySource<IJp2pProperties> source) {
		jas = new Jp2pAdvertisementService<U>((IJp2pWritePropertySource<IJp2pProperties>) super.getPropertySource(), advertisement, service );
		return super.onCreateComponent(source);
	}
}
