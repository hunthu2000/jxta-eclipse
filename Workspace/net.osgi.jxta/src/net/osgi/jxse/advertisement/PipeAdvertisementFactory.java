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
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Directives;
import net.osgi.jxse.properties.IJxsePropertySource;

public class PipeAdvertisementFactory extends AbstractComponentFactory<PipeAdvertisement, net.osgi.jxse.advertisement.IPipeAdvertisementFactory.Properties, Directives> implements IPipeAdvertisementFactory {

	public static final String S_PIPE_ADVERTISEMENT_SERVICE = "PipeAdvertisementService";

	public final static String SOCKETIDSTR = "urn:jxta:uuid-59616261646162614E5047205032503393B5C2F6CA7A41FBB0F890173088E79404";
	public final static String DEFAULT_SOCKET_NAME = "Default Socket Server";

	public PipeAdvertisementFactory() {
		super( null );
		this.fillDefaultValues();
	}

	protected void fillDefaultValues() {
		IJxsePropertySource<Properties, Directives> source = super.getPropertySource();
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

	protected PipeAdvertisement onCreateModule() {
		PipeID socketID = null;
		IJxsePropertySource<Properties, Directives> source = super.getPropertySource();
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
		return advertisement;
	}

	@Override
	protected void onParseDirectivePriorToCreation(
			net.osgi.jxse.factory.IComponentFactory.Directives directive,
			Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onParseDirectiveAfterCreation(PipeAdvertisement module,
			net.osgi.jxse.factory.IComponentFactory.Directives directive,
			Object value) {
		// TODO Auto-generated method stub
		
	}
}
