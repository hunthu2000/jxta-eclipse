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
package net.osgi.jxse.advertisement;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.JxseComponent;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class PipeAdvertisementFactory extends AbstractComponentFactory<PipeAdvertisement> implements IPipeAdvertisementFactory {

	public static final String S_PIPE_ADVERTISEMENT_SERVICE = "PipeAdvertisementService";

	public final static String SOCKETIDSTR = "urn:jxta:uuid-59616261646162614E5047205032503393B5C2F6CA7A41FBB0F890173088E79404";
	public final static String DEFAULT_SOCKET_NAME = "Default Socket Server";

	public PipeAdvertisementFactory( BuilderContainer container) {
		super( container );
		this.fillDefaultValues();
	}

	protected void fillDefaultValues() {
		IJxseWritePropertySource<IJxseProperties> source = (IJxseWritePropertySource<IJxseProperties>) super.getPropertySource();
		try {
			source.setProperty( Properties.SOCKET_ID, new URI( SOCKETIDSTR ));
		} catch (URISyntaxException e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
		}
		source.setProperty( Properties.NAME, DEFAULT_SOCKET_NAME );
		source.setProperty( Properties.TYPE, PipeService.UnicastType );		
	}
	
	@Override
	public String getComponentName() {
		return S_PIPE_ADVERTISEMENT_SERVICE;
	}

	@Override
	protected IJxsePropertySource<IJxseProperties> onCreatePropertySource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IJxseComponent<PipeAdvertisement> onCreateComponent( IJxsePropertySource<IJxseProperties> properties) {
		PipeID socketID = null;
		IJxsePropertySource<IJxseProperties> source = super.getPropertySource();
		try {
			socketID = (PipeID) IDFactory.fromURI( (URI) source.getProperty( Properties.SOCKET_ID ));
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}
		PipeAdvertisement advertisement = (PipeAdvertisement)
				AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
		advertisement.setPipeID(socketID);
		advertisement.setType( (String) source.getProperty( Properties.TYPE ));
		advertisement.setName( (String) source.getProperty( Properties.NAME ));
		return new JxseComponent<PipeAdvertisement>( advertisement );
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
