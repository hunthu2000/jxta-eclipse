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
package net.osgi.jp2p.jxta.socket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.component.Jp2pComponent;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class SocketPipeAdvertisementFactory extends PipeAdvertisementFactory {

	public final static String SOCKETIDSTR = "urn:jxta:uuid-59616261646162614E5047205032503393B5C2F6CA7A41FBB0F890173088E79404";
	public final static String DEFAULT_SOCKET_NAME = "Default Socket Server";

	public enum Properties implements IJp2pProperties{
		SOCKET_ID,
		NAME,
		TYPE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
	}
	
	public SocketPipeAdvertisementFactory( IContainerBuilder container) {
		super( container);
		this.fillDefaultValues();
	}

	@Override
	protected void fillDefaultValues() {
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) super.getPropertySource();		
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
	public IJp2pComponent<PipeAdvertisement> createComponent() {
		PipeID socketID = null;
		IJp2pPropertySource<Properties> source = null;//TODOsuper.getPropertySource();		
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
		super.setCompleted(true);
		return new Jp2pComponent<PipeAdvertisement>(  super.getPropertySource(), advertisement);
	}
}
