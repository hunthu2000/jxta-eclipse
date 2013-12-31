package net.osgi.jp2p.properties;

import net.osgi.jp2p.properties.AbstractJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public abstract class AbstractPeerGroupProviderPropertySource<E extends Object> extends AbstractJp2pWritePropertySource
{

	public enum PeerGroupDirectives implements IJp2pDirectives{
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

	public AbstractPeerGroupProviderPropertySource( String componentName, IJp2pPropertySource<IJp2pProperties> parent) {
		super( componentName,parent );
	}
}
