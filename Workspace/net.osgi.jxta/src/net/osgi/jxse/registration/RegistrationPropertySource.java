package net.osgi.jxse.registration;

import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.utils.StringStyler;

public class RegistrationPropertySource extends AbstractJxseWritePropertySource<IJxseProperties>
{
	public enum RegistrationProperties implements IJxseProperties{
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

	public enum RegistrationDirectives implements IJxseDirectives{
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

	public RegistrationPropertySource( IJxsePropertySource<?> parent) {
		this( Components.REGISTRATION_SERVICE.toString(), parent );
	}


	public RegistrationPropertySource( String componentName, IJxsePropertySource<?> parent) {
		super( componentName,parent );
		this.fillDefaultValues();
	}

	protected void fillDefaultValues() {
		this.setManagedProperty( new ManagedProperty< IJxseProperties, Object>( RegistrationProperties.WAIT_TIME, 60000, true ));
		this.setManagedProperty( new ManagedProperty< IJxseProperties, Object>( RegistrationProperties.PEER_ID, null, true ));
		this.setManagedProperty( new ManagedProperty< IJxseProperties, Object>( RegistrationProperties.ATTRIBUTE, null, true ));
		this.setManagedProperty( new ManagedProperty< IJxseProperties, Object>( RegistrationProperties.WILDCARD, null, true ));
		this.setManagedProperty( new ManagedProperty< IJxseProperties, Object>( RegistrationProperties.THRESHOLD, 1, true ));
	}

	@Override
	public IJxseDirectives getDirectiveFromString(String id) {
		if( RegistrationDirectives.isValidDirective( id ))
			return RegistrationDirectives.valueOf( id );
		return super.getDirectiveFromString(id);
	}


	@Override
	public RegistrationProperties getIdFromString(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean validate(IJxseProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}
