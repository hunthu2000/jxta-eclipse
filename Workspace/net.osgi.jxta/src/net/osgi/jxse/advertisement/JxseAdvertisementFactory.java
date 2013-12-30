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

import org.xml.sax.Attributes;

import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.ID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.JxseComponent;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.peergroup.PeerGroupFactory;
import net.osgi.jxse.pipe.PipePropertySource.PipeProperties;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class JxseAdvertisementFactory extends AbstractComponentFactory<Advertisement> {

	public static final String S_DEFAULT_NAME = "Default JXTA Advertisement";

	public static final String S_ERR_MUST_CREATE_ID = "The component has to create an id, but no id is provided. Please do this fisrt";

	public static final String S_ADVERTISEMENT_SERVICE = "AdvertisementService";
	
	private PeerGroup peergroup;

	public JxseAdvertisementFactory( BuilderContainer container, IJxsePropertySource<IJxseProperties> parentSource) {
		super( container, parentSource );
	}

	@Override
	public String getComponentName() {
		return Components.PIPE_SERVICE.toString();
	}

	@Override
	protected AdvertisementPropertySource onCreatePropertySource() {
		AdvertisementPropertySource source = new AdvertisementPropertySource( super.getParentSource() );
		return source;
	}

	@Override
	protected IJxseComponent<Advertisement> onCreateComponent( IJxsePropertySource<IJxseProperties> properties) {
		IJxsePropertySource<IJxseProperties> source = super.getPropertySource();
		String tp = StringStyler.styleToEnum((String) source.getDirective( AdvertisementDirectives.TYPE ));
		if( Utils.isNull(tp))
			return null;
		AdvertisementTypes type = AdvertisementTypes.valueOf(tp);
		switch( type ){
		case PIPE:
			return new JxseComponent<Advertisement>( this.createPipeAdvertisement());
		default:
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void checkDiscoveryService(){
		IJxseWritePropertySource<IJxseProperties> source = (IJxseWritePropertySource<IJxseProperties>) super.getPropertySource();
		IJxseWritePropertySource<IJxseProperties> dsource = (IJxseWritePropertySource<IJxseProperties>) source.getChild( Components.DISCOVERY_SERVICE.toString() );
		if( dsource == null )
			return;
		
	}
	
	protected PipeAdvertisement createPipeAdvertisement(){
		IJxseWritePropertySource<IJxseProperties> source = (IJxseWritePropertySource<IJxseProperties>) super.getPropertySource();
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
	public static AdvertisementTypes getAdvertisementType( Attributes attrs, String qName ,IJxsePropertySource<IJxseProperties> parent ){
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
	protected AdvertisementPropertySource getAdvertisementPropertysource( Attributes attrs, String qName ,IJxsePropertySource<IJxseProperties> parent ){
		AdvertisementPropertySource source = new AdvertisementPropertySource( qName, parent );
		AdvertisementTypes adv_type = this.getAdvertisementType(attrs, qName, parent);
		//switch( adv_type ){
		//case PIPE:
		//	return new PipePropertySource( source );
		//default:
			return source;
		//}
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		String name = StringStyler.styleToEnum(event.getFactory().getComponentName());
		if( !Components.isComponent(name))
			return;

		switch( event.getBuilderEvent()){
		case COMPONENT_STARTED:
			if( !isComponentFactory( Components.PEERGROUP_SERVICE, event.getFactory() ) && 
					!isComponentFactory( Components.NET_PEERGROUP_SERVICE, event.getFactory() ))
				return;
			IComponentFactory<?> factory = event.getFactory();
			if( !PeerGroupFactory.isCorrectPeerGroup( this.getPropertySource(), factory))
				return;
			peergroup = PeerGroupFactory.getPeerGroup( factory );
			super.setCanCreate( peergroup != null );
			super.startComponent();
			break;
		default:
			break;
		}
		super.notifyChange(event);
	}

}
