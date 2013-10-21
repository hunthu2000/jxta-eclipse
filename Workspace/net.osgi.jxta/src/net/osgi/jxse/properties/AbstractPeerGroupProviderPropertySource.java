package net.osgi.jxse.properties;

import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;

public abstract class AbstractPeerGroupProviderPropertySource<E extends Enum<E>> extends AbstractJxseWritePropertySource<E, IJxseDirectives>
{

	public enum PeerGroupDirectives implements IJxseDirectives{
		PEERGROUP;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
		
		public static final boolean isValidDirective( String key){
			for( PeerGroupDirectives directive: PeerGroupDirectives.values() ){
				if( directive.name().equals( key ))
					return true;
			}
			return false;
		}
	}

	public AbstractPeerGroupProviderPropertySource( String componentName, IJxsePropertySource<?,IJxseDirectives> parent) {
		super( componentName,parent );
	}

	@Override
	public IJxseDirectives getDirectiveFromString(String id) {
		if( PeerGroupDirectives.isValidDirective( id ))
			return PeerGroupDirectives.valueOf( id );
		return super.getDirectiveFromString(id);
	}
}
