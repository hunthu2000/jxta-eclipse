package net.osgi.jp2p.jxta.registration;

import net.osgi.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.properties.AbstractJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.ManagedProperty;

public class RegistrationPropertySource extends AbstractJp2pWritePropertySource
{
	public enum RegistrationProperties implements IJp2pProperties{
		DISCOVERY_MODE,
		WAIT_TIME,
		PEER_ID,
		ATTRIBUTE,
		WILDCARD,
		THRESHOLD;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public enum RegistrationDirectives implements IJp2pDirectives{
		PEERGROUP;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
		
		public static final boolean isValidDirective( String key){
			for( RegistrationDirectives directive: RegistrationDirectives.values() ){
				if( directive.name().equals( key ))
					return true;
			}
			return false;
		}
	}

	public RegistrationPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		this( JxtaComponents.REGISTRATION_SERVICE.toString(), parent );
	}


	public RegistrationPropertySource( String componentName, IJp2pPropertySource<IJp2pProperties> parent) {
		super( componentName,parent );
		this.fillDefaultValues();
	}

	protected void fillDefaultValues() {
		this.setManagedProperty( new ManagedProperty< IJp2pProperties, Object>( RegistrationProperties.WAIT_TIME, 60000, true ));
		this.setManagedProperty( new ManagedProperty< IJp2pProperties, Object>( RegistrationProperties.PEER_ID, null, true ));
		this.setManagedProperty( new ManagedProperty< IJp2pProperties, Object>( RegistrationProperties.ATTRIBUTE, null, true ));
		this.setManagedProperty( new ManagedProperty< IJp2pProperties, Object>( RegistrationProperties.WILDCARD, null, true ));
		this.setManagedProperty( new ManagedProperty< IJp2pProperties, Object>( RegistrationProperties.THRESHOLD, 1, true ));
	}

	@Override
	public RegistrationProperties getIdFromString(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean validate(IJp2pProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}
