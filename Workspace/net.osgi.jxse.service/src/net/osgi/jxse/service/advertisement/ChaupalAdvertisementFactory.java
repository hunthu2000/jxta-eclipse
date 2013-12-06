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

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementProperties;
import net.osgi.jxse.advertisement.JxseAdvertisementFactory;
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.discovery.DiscoveryPropertySource.DiscoveryProperties;
import net.osgi.jxse.discovery.DiscoveryServiceFactory;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class ChaupalAdvertisementFactory extends
		AbstractComponentFactory<JxseAdvertisementService, IJxseProperties, IJxseDirectives> {

	public static final String S_DISCOVERY_SERVICE = "JxseDiscoveryService";

	private ComponentNode<Advertisement, DiscoveryProperties, IJxseDirectives> node;

	public ChaupalAdvertisementFactory( JxseAdvertisementFactory factory ) {
		super( factory.getPropertySource() );
	}

	@Override
	protected JxseAdvertisementService onCreateModule( IJxsePropertySource<IJxseProperties, IJxseDirectives> properties) {
		//JxseAdvertisementService ds = node.getFactory().createModule();
		//if( ds == null )
		//	return null;
		//JxseAdvertisementService service = new JxseAdvertisementService( factory );
		return null;//service;
	}
}