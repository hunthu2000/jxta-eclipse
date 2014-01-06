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

import org.xml.sax.Attributes;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.ID;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.component.Jp2pComponent;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.jxta.discovery.DiscoveryServiceFactory;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.factory.AbstractDependencyFactory;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.jxta.peergroup.PeerGroupFactory;
import net.osgi.jp2p.jxta.pipe.PipePropertySource.PipeProperties;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class Jp2pAdvertisementFactory extends AbstractDependencyFactory<Advertisement, IJp2pComponent<DiscoveryService>> {

	public static final String S_DEFAULT_NAME = "Default JXTA Advertisement";

	public static final String S_ERR_MUST_CREATE_ID = "The component has to create an id, but no id is provided. Please do this fisrt";

	public static final String S_ADVERTISEMENT_SERVICE = "AdvertisementService";
	
	public Jp2pAdvertisementFactory( ContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parentSource) {
		super( container, parentSource );
	}

	@Override
	public String getComponentName() {
		return JxtaComponents.ADVERTISEMENT_SERVICE.toString();
	}

	@Override
	protected AdvertisementPropertySource onCreatePropertySource() {
		AdvertisementPropertySource source = new AdvertisementPropertySource( super.getParentSource() );
		return source;
	}

	@Override
	protected boolean isCorrectFactory(IComponentFactory<?> factory) {
		if( !(factory instanceof DiscoveryServiceFactory  ))
			return false;
		String peergroup = PeerGroupFactory.findAncestorPeerGroup(this.getPropertySource() );
		String fpg = PeerGroupFactory.findAncestorPeerGroup(factory.getPropertySource() );
		return peergroup.equals(fpg);
	}

	@Override
	protected IJp2pComponent<Advertisement> onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		IJp2pPropertySource<IJp2pProperties> source = super.getPropertySource();
		String tp = StringStyler.styleToEnum((String) source.getDirective( AdvertisementDirectives.TYPE ));
		if( Utils.isNull(tp))
			return null;
		AdvertisementTypes type = AdvertisementTypes.valueOf(tp);
		switch( type ){
		case PIPE:
			return new Jp2pComponent<Advertisement>( this.createPipeAdvertisement());
		default:
			return null;
		}
	}
	
	protected PipeAdvertisement createPipeAdvertisement(){
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) super.getPropertySource();
		AdvertisementTypes type = AdvertisementTypes.valueOf( StringStyler.styleToEnum( (String) source.getDirective( AdvertisementDirectives.TYPE )));
		AdvertisementPreferences preferences = new AdvertisementPreferences( source );
		PipeAdvertisement pipead = ( PipeAdvertisement )AdvertisementFactory.newAdvertisement( AdvertisementTypes.convertTo(type));
		preferences.createDefaultValue( PipeProperties.PIPE_ID);
		pipead.setPipeID( (ID) source.getProperty( PipeProperties.PIPE_ID ));
		pipead.setType((String) source.getProperty(PipeProperties.TYPE ));
		return pipead;
	}

	/**
	 * Get the correct advertisement type
	 * @param attrs
	 * @param qName
	 * @param parent
	 * @return
	 */
	public static AdvertisementTypes getAdvertisementType( Attributes attrs, String qName ,IJp2pPropertySource<IJp2pProperties> parent ){
		if(( attrs == null ) || ( attrs.getLength() == 0))
				return AdvertisementTypes.ADV;
		String type = attrs.getValue(AdvertisementDirectives.TYPE.toString().toLowerCase() );
		if( Utils.isNull(type))
			return AdvertisementTypes.ADV;
		return AdvertisementTypes.valueOf( StringStyler.styleToEnum( type ));
	}	

	/**
	 * Get the correct property source
	 * @param attrs
	 * @param qName
	 * @param parent
	 * @return
	 */
	protected AdvertisementPropertySource getAdvertisementPropertysource( Attributes attrs, String qName ,IJp2pPropertySource<IJp2pProperties> parent ){
		AdvertisementPropertySource source = new AdvertisementPropertySource( qName, parent );
		AdvertisementTypes adv_type = this.getAdvertisementType(attrs, qName, parent);
		//switch( adv_type ){
		//case PIPE:
		//	return new PipePropertySource( source );
		//default:
			return source;
		//}
	}
}
