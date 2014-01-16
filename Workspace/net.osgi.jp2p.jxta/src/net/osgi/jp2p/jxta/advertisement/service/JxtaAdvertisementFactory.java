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
package net.osgi.jp2p.jxta.advertisement.service;

import java.net.URISyntaxException;

import org.xml.sax.Attributes;

import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.ID;
import net.jxta.pipe.PipeID;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.component.Jp2pComponent;
import net.osgi.jp2p.jxta.advertisement.service.AdvertisementServicePropertySource.AdvertisementDirectives;
import net.osgi.jp2p.jxta.advertisement.service.AdvertisementServicePropertySource.AdvertisementServiceProperties;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.jxta.advertisement.ModuleClassAdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.ModuleImplAdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.ModuleSpecAdvertisementPropertySource;
import net.osgi.jp2p.jxta.factory.AbstractPeerGroupDependencyFactory;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.jxta.peergroup.PeerGroupAdvertisementPropertySource;
import net.osgi.jp2p.jxta.pipe.PipeAdvertisementPreferences;
import net.osgi.jp2p.jxta.pipe.PipeAdvertisementPropertySource;
import net.osgi.jp2p.jxta.pipe.PipePropertySource.PipeServiceProperties;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class JxtaAdvertisementFactory extends AbstractPeerGroupDependencyFactory<Advertisement> {
	
	private AdvertisementTypes type;
	
	public JxtaAdvertisementFactory( IContainerBuilder container, AdvertisementTypes type, IJp2pPropertySource<IJp2pProperties> parentSource) {
		super( container, parentSource );
		this.type = type;
	}

	@Override
	public String getComponentName() {
		return JxtaComponents.ADVERTISEMENT.toString();
	}

	
	@Override
	protected AdvertisementPropertySource onCreatePropertySource() {
		AdvertisementPropertySource source;
		switch( type ){
		case MODULE_CLASS:
			source = new ModuleClassAdvertisementPropertySource(super.getParentSource() );
			break;
		case MODULE_SPEC:
			source = new ModuleSpecAdvertisementPropertySource(super.getParentSource() );
			break;
		case MODULE_IMPL:
			source = new ModuleImplAdvertisementPropertySource(super.getParentSource() );
			break;
		case PEERGROUP:
			source = new PeerGroupAdvertisementPropertySource(super.getParentSource() );
			break;
		case PEER:
			source = null;//new PeerAdvertisementPropertySource(super.getParentSource() );
			break;
		case PIPE:
			source = new PipeAdvertisementPropertySource(super.getParentSource() );
			break;
		default:
			source = new AdvertisementPropertySource( super.getParentSource() );
			break;
		}
		return source;
	}

	
	@Override
	public void extendContainer() {
		AdvertisementTypes type = AdvertisementTypes.convertFrom((String) super.getPropertySource().getDirective( AdvertisementDirectives.TYPE ));
		if( !AdvertisementTypes.MODULE_SPEC.equals( type ))
			return;
		ModuleClassAdvertisementPropertySource msps = (ModuleClassAdvertisementPropertySource) AdvertisementPropertySource.findAdvertisementDescendant(super.getPropertySource(), AdvertisementTypes.MODULE_CLASS );
		if( msps == null )
			super.getPropertySource().addChild( new ModuleClassAdvertisementPropertySource( super.getPropertySource() ));
		// TODO Auto-generated method stub
		super.extendContainer();
	}

	@Override
	protected IJp2pComponent<Advertisement> onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		IJp2pPropertySource<IJp2pProperties> source = super.getPropertySource();
		String tp = StringStyler.styleToEnum((String) source.getDirective( AdvertisementDirectives.TYPE ));
		if( Utils.isNull(tp))
			return null;
		Advertisement adv = null;
		AdvertisementTypes type = AdvertisementTypes.valueOf(tp);
		switch( type ){
		case PIPE:
			try {
				adv = this.createPipeAdvertisement();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			break;
		default:
			return null;
		}
		return new Jp2pComponent<Advertisement>((IJp2pWritePropertySource<IJp2pProperties>) source, adv );
	}

	/**
	 * Create a pipe advertisement
	 * @return
	 * @throws URISyntaxException 
	 */
	protected PipeAdvertisement createPipeAdvertisement() throws URISyntaxException{
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) super.getPropertySource();
		AdvertisementTypes type = AdvertisementTypes.valueOf( StringStyler.styleToEnum( (String) source.getDirective( AdvertisementDirectives.TYPE )));
		PipeAdvertisementPreferences preferences = new PipeAdvertisementPreferences( source, super.getDependency().getModule());
		PipeAdvertisement pipead = ( PipeAdvertisement )AdvertisementFactory.newAdvertisement( AdvertisementTypes.convertTo(type));
		PipeID pipeId = preferences.getPipeID();
		pipead.setPipeID( (ID) pipeId );
		pipead.setType((String) source.getProperty(PipeServiceProperties.TYPE ));
		String name = (String) source.getProperty( AdvertisementServiceProperties.NAME );
		pipead.setName(name);
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
}
