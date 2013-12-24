package net.osgi.jxse.properties;

import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;

public abstract class AbstractPeerGroupProviderPropertySource<E extends Object> extends AbstractJxseWritePropertySource
{

	public enum PeerGroupDirectives implements IJxseDirectives{
		TYPE,
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

	public AbstractPeerGroupProviderPropertySource( String componentName, IJxsePropertySource<IJxseProperties> parent) {
		super( componentName,parent );
	}
}
