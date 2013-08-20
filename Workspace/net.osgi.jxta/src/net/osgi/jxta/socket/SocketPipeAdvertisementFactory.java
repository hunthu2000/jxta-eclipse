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
package net.osgi.jxta.socket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jxta.advertisement.PipeAdvertisementFactory;
import net.osgi.jxta.utils.StringStyler;

public class SocketPipeAdvertisementFactory extends PipeAdvertisementFactory {

	public final static String SOCKETIDSTR = "urn:jxta:uuid-59616261646162614E5047205032503393B5C2F6CA7A41FBB0F890173088E79404";
	public final static String DEFAULT_SOCKET_NAME = "Default Socket Server";

	public enum Properties{
		SOCKET_ID,
		NAME,
		TYPE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
	}
	
	public SocketPipeAdvertisementFactory() {
		super( );
		this.fillDefaultValues();
	}

	@Override
	protected void fillDefaultValues() {
		try {
			super.addProperty( Properties.SOCKET_ID, new URI( SOCKETIDSTR ));
		} catch (URISyntaxException e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
		}
		super.addProperty( Properties.NAME, DEFAULT_SOCKET_NAME );
		super.addProperty( Properties.TYPE, PipeService.UnicastType );		
	}


	@Override
	public PipeAdvertisement createModule() {
		PipeID socketID = null;
		try {
			socketID = (PipeID) IDFactory.fromURI( (URI) super.getProperty( Properties.SOCKET_ID ));
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}
		PipeAdvertisement advertisement = (PipeAdvertisement)
				AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
		advertisement.setPipeID(socketID);
		advertisement.setType( (String) super.getProperty( Properties.TYPE ));
		advertisement.setName( (String) super.getProperty( Properties.NAME ));
		super.setCompleted(true);
		return advertisement;
	}
}
