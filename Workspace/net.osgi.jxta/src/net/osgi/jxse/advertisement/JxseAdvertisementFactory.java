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

import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.ID;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jxse.advertisement.PipeAdvertisementPropertySource.PipeAdvertisementProperties;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.utils.StringStyler;

public class JxseAdvertisementFactory extends AbstractComponentFactory<Advertisement, IJxseProperties, IJxseDirectives> {

	public static final String S_DEFAULT_NAME = "Default JXTA Advertisement";

	public static final String S_ERR_MUST_CREATE_ID = "The component has to create an id, but no id is provided. Please do this fisrt";

	public static final String S_ADVERTISEMENT_SERVICE = "AdvertisementService";

	private IPeerGroupProvider peerGroupContainer;

	public JxseAdvertisementFactory( IPeerGroupProvider peerGroupContainer, IJxsePropertySource<IJxseProperties, IJxseDirectives> source ) {
		super( source );
		this.peerGroupContainer = peerGroupContainer;
	}
	
	@Override
	protected Advertisement onCreateModule( IJxsePropertySource<IJxseProperties, IJxseDirectives> properties) {
		if( peerGroupContainer.getPeerGroup() == null ){
			super.setCompleted( false );
			return null;
		}
			
		IJxsePropertySource<IJxseProperties, IJxseDirectives> source = super.getPropertySource();
		String tp = StringStyler.styleToEnum((String) source.getDirective( AdvertisementDirectives.TYPE ));
		AdvertisementTypes type = AdvertisementTypes.valueOf(tp);
		switch( type ){
		case PIPE:
			return this.createPipeAdvertisement();
		default:
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void checkDiscoveryService(){
		IJxseWritePropertySource<IJxseProperties, IJxseDirectives> source = (IJxseWritePropertySource<IJxseProperties, IJxseDirectives>) super.getPropertySource();
		IJxseWritePropertySource<IJxseProperties, IJxseDirectives> dsource = (IJxseWritePropertySource<IJxseProperties, IJxseDirectives>) source.getChild( Components.DISCOVERY_SERVICE.toString() );
		if( dsource == null )
			return;
		
	}
	
	protected PipeAdvertisement createPipeAdvertisement(){
		IJxseWritePropertySource<IJxseProperties, IJxseDirectives> source = (IJxseWritePropertySource<IJxseProperties, IJxseDirectives>) super.getPropertySource();
		AdvertisementTypes type = AdvertisementTypes.valueOf( StringStyler.styleToEnum( (String) source.getDirective( AdvertisementDirectives.TYPE )));
		AdvertisementPreferences preferences = new AdvertisementPreferences( this.peerGroupContainer, source );
		PipeAdvertisement pipead = ( PipeAdvertisement )AdvertisementFactory.newAdvertisement( AdvertisementTypes.convertTo(type));
		preferences.createDefaultValue( PipeAdvertisementProperties.PIPE_ID);
		pipead.setPipeID( (ID) source.getProperty( PipeAdvertisementProperties.PIPE_ID ));
		pipead.setType((String) source.getProperty(PipeAdvertisementProperties.TYPE ));
		return pipead;
	}
}
