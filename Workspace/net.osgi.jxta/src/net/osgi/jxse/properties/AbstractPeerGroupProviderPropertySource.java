package net.osgi.jxse.properties;

import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public abstract class AbstractPeerGroupProviderPropertySource<E extends Object> extends AbstractJxseWritePropertySource
{

	public enum PeerGroupDirectives implements IJxseDirectives{
		TYPE,
		PEERGROUP;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
		
		public static boolean isValidDirective( String str ){
			if( Utils.isNull( str ))
				return false;
			for( PeerGroupDirectives dir: values() ){
				if( dir.name().equals( str ))
					return true;
			}
			return false;
		}

	}

	public AbstractPeerGroupProviderPropertySource( String componentName, IJxsePropertySource<IJxseProperties> parent) {
		super( componentName,parent );
	}
}
