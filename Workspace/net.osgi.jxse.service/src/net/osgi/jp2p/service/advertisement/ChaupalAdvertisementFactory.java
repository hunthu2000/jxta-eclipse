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
package net.osgi.jp2p.service.advertisement;

import net.jxta.document.Advertisement;
import net.osgi.jp2p.advertisement.Jp2pAdvertisementFactory;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class ChaupalAdvertisementFactory extends Jp2pAdvertisementFactory{

	@SuppressWarnings("unchecked")
	public ChaupalAdvertisementFactory( ContainerBuilder container, IComponentFactory<Advertisement> factory ) {
		super( container, (IJp2pPropertySource<IJp2pProperties>) factory.getPropertySource().getParent() );
		super.setSource(factory.createPropertySource());
	}

	@Override
	public String getComponentName() {
		return Components.ADVERTISEMENT_SERVICE.toString();
	}

	@Override
	public void extendContainer() {
		ContainerBuilder builder = super.getBuilder();
		IComponentFactory<?> df = builder.getFactory( Components.DISCOVERY_SERVICE.toString());
		if( df == null){
			builder.addFactoryToContainer( Components.DISCOVERY_SERVICE.toString(), this, false, false); 
		}
		super.extendContainer();
	}

	@Override
	protected Jp2pAdvertisementService onCreateComponent( IJp2pPropertySource<IJp2pProperties> source) {
		IJp2pComponent<Advertisement> ds = super.onCreateComponent( source );
		Jp2pAdvertisementService service = new Jp2pAdvertisementService( (IJp2pWritePropertySource<IJp2pProperties>) source, ds.getModule() );
		return service;
	}
}