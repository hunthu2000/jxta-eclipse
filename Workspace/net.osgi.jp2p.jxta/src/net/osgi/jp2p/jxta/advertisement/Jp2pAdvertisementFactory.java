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
package net.osgi.jp2p.jxta.advertisement;

import net.jxta.document.Advertisement;
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.factory.AbstractFilterFactory;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.filter.AbstractComponentFactoryFilter;
import net.osgi.jp2p.filter.FilterChain;
import net.osgi.jp2p.filter.FilterChain.Operators;
import net.osgi.jp2p.filter.IComponentFactoryFilter;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public class Jp2pAdvertisementFactory<T extends Advertisement> extends AbstractFilterFactory<T> {

	private Advertisement advertisment;
	
	public Jp2pAdvertisementFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parentSource) {
		super( container, parentSource );
	}

	protected Advertisement getAdvertisment() {
		return advertisment;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected IComponentFactoryFilter createFilter() {
		FilterChain<T> bf = new FilterChain<T>( Operators.SEQUENTIAL_AND, (IComponentFactory<T>) this );
		IComponentFactoryFilter filter = new AbstractComponentFactoryFilter<T>((IComponentFactory<T>) this){

			@Override
			public boolean onAccept(ComponentBuilderEvent<?> event) {
				switch( event.getBuilderEvent() ){
				case COMPONENT_CREATED:
					Advertisement tempad = null;
					IJp2pPropertySource<IJp2pProperties> source = event.getFactory().getPropertySource();
					if( event.getFactory().getComponent() instanceof IJp2pComponent ){
						IJp2pComponent<?> comp = (IJp2pComponent<?>) event.getFactory().getComponent();
						if( comp.getModule() instanceof Advertisement )
							tempad = (Advertisement) comp.getModule();
						else
							return false;
					}
					if( tempad == null ){
						if( event.getFactory().getComponent() instanceof Advertisement)
							tempad = (Advertisement) event.getFactory().getComponent();
						else
							return false;
					}
					
					if( isChild( source )){
						advertisment = (Advertisement) tempad;
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
	 * Returns true if the property source is a child of the parent. 
	 * @param source
	 * @return
	 */
	protected boolean isChild( IJp2pPropertySource<?> source ){
		return AdvertisementPropertySource.isChild( this.getPropertySource(), source );
	}

	@Override
	protected AdvertisementPropertySource onCreatePropertySource() {
		AdvertisementPropertySource source = new AdvertisementPropertySource( JxtaComponents.ADVERTISEMENT_SERVICE.toString(), super.getParentSource() );
		return source;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected IJp2pComponent<T> onCreateComponent( IJp2pPropertySource<IJp2pProperties> source) {
		AdvertisementPropertySource adsource = (AdvertisementPropertySource) source.getChild( JxtaComponents.ADVERTISEMENT.toString() );
		JxtaAdvertisementFactory factory = (JxtaAdvertisementFactory) super.getBuilder().getFactory( adsource );
		return (IJp2pComponent<T>) factory.getComponent();
	}
}
