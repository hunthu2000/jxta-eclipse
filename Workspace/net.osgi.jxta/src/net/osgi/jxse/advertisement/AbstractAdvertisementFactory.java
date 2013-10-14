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
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;

public abstract class AbstractAdvertisementFactory<T extends Advertisement, U extends Enum<U>, V extends IJxseDirectives> extends AbstractComponentFactory<T,U,V> {

	public enum AdvertisementTypes{
		ADV,
		PEERGROUP,
		MODULE_CLASS,
		MODULE_SPEC,
		PIPE;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
		
		/**
		 * Convert the enum to a form that the jxta lib can understand
		 * @param adType
		 * @return
		 */
		public static String convert( AdvertisementTypes adType ){
			switch( adType ){
			case PEERGROUP:
				return PeerGroupAdvertisement.getAdvertisementType();
			case PIPE:
				return PipeAdvertisement.getAdvertisementType();
			case MODULE_CLASS:
				return ModuleClassAdvertisement.getAdvertisementType();
			case MODULE_SPEC:
				return ModuleSpecAdvertisement.getAdvertisementType();
			default:
				return Advertisement.getAdvertisementType();
			}
		}
	
		/**
		 * Concert from advertisement type 
		*/
		public static AdvertisementTypes convert( String type ){
			if( type.equals( PeerGroupAdvertisement.getAdvertisementType()))
				return AdvertisementTypes.PEERGROUP;
			if( type.equals(PipeAdvertisement.getAdvertisementType()))
				  return AdvertisementTypes.PIPE;
			if( type.equals(ModuleClassAdvertisement.getAdvertisementType()))
				  return AdvertisementTypes.MODULE_CLASS;
			if( type.equals(ModuleSpecAdvertisement.getAdvertisementType()))
				  return AdvertisementTypes.MODULE_SPEC;
			return AdvertisementTypes.ADV;
		}
	
	}

	public enum AdvertisementProperties{
		ADVERTISEMENT_TYPE,
		NAME,
		ADV_TYPE,
		ID;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
	}

	public static final String S_DEFAULT_NAME = "Default JXTA Advertisement";

	public static final String S_ERR_MUST_CREATE_ID = "The component has to create an id, but no id is provided. Please do this fisrt";

	public static final String S_ADVERTISEMENT_SERVICE = "AdvertisementService";
	
	protected AbstractAdvertisementFactory( IJxsePropertySource<U,V> source ) {
		super( source );
		this.fillDefaultValues();	
	}

	
	@SuppressWarnings("unchecked")
	protected void fillDefaultValues() {
		//super.getPropertySource().setProperty((U) AdvertisementProperties.ADVERTISEMENT_TYPE, AdvertisementTypes.ADV);
		//super.getPropertySource().setProperty((U) AdvertisementProperties.NAME, S_DEFAULT_NAME);
		//if( this.createId )
		//	super.addProperty( AdvertisementProperties.ID, IDFactory.newPipeID(net.jxta.peergroup.PeerGroupID.defaultNetPeerGroupID));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T createModule() {
		//if( this.createId && ( super.getProperty( AdvertisementProperties.ID ) == null ))
		//	throw new IllegalStateException( S_ERR_MUST_CREATE_ID );
		AdvertisementTypes adType = ( AdvertisementTypes )super.getPropertySource().getProperty((U) AdvertisementProperties.ADVERTISEMENT_TYPE );
		T advertisement = (T)AdvertisementFactory.newAdvertisement( AdvertisementTypes.convert(adType));
		return advertisement;
	}

}
