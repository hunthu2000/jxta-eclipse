package org.chaupal.jp2p.ui.jxta.osgi.service;

import net.jp2p.container.properties.AbstractJp2pWritePropertySource;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.utils.StringStyler;
import net.osgi.jp2p.chaupal.Activator;

public class PetitionPropertySource extends AbstractJp2pWritePropertySource
{
	public static final String S_PETITIONER = "JP2P Container Petitioner";
	public static final long DEFAULT_TIME_OUT = 500; //msec

	public enum PetitionerProperties implements IJp2pProperties{
		REFRESH_TIME;

		/**
		 * Returns true if the given property is valid for this enumeration
		 * @param property
		 * @return
		 */
		public static boolean isValidProperty( IJp2pProperties property ){
			if( property == null )
				return false;
			for( PetitionerProperties prop: values() ){
				if( prop.equals( property ))
					return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public PetitionPropertySource() {
		super( Activator.BUNDLE_ID, S_PETITIONER );
		super.setProperty( PetitionerProperties.REFRESH_TIME, DEFAULT_TIME_OUT);
	}

	@Override
	public PetitionerProperties getIdFromString(String key) {
		return PetitionerProperties.valueOf( key );
	}
}