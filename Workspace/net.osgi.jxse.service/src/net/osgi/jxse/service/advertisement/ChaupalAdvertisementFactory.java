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
package net.osgi.jxse.service.advertisement;

import net.jxta.document.Advertisement;
import net.osgi.jxse.advertisement.JxseAdvertisementFactory;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class ChaupalAdvertisementFactory extends JxseAdvertisementFactory{

	public static final String S_DISCOVERY_SERVICE = "JxseDiscoveryService";

	@SuppressWarnings("unchecked")
	public ChaupalAdvertisementFactory( BuilderContainer container, IComponentFactory<Advertisement> factory ) {
		super( container, (IJxsePropertySource<IJxseProperties>) factory.getPropertySource().getParent() );
		super.setSource(factory.createPropertySource());
	}

	@Override
	public String getComponentName() {
		return S_DISCOVERY_SERVICE;
	}

	@Override
	protected JxseAdvertisementService onCreateComponent( IJxsePropertySource<IJxseProperties> source) {
		IJxseComponent<Advertisement> ds = super.onCreateComponent( source );
		JxseAdvertisementService service = new JxseAdvertisementService( (IJxseWritePropertySource<IJxseProperties>) source, ds.getModule() );
		return service;
	}
}