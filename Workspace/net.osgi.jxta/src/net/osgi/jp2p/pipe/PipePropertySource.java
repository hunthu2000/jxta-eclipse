package net.osgi.jp2p.pipe;

import net.osgi.jp2p.factory.IComponentFactory.Components;
import net.osgi.jp2p.peergroup.PeerGroupPropertySource;
import net.osgi.jp2p.properties.AbstractJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class PipePropertySource extends AbstractJp2pWritePropertySource{

	public enum PipeProperties implements IJp2pProperties{
		PIPE_ID,
		TYPE;
	
		public static boolean isValidProperty( String str ){
			if( Utils.isNull( str ))
				return false;
			for( PipeProperties dir: values() ){
				if( dir.name().equals( str ))
					return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public PipePropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( Components.PIPE_SERVICE.toString(), parent);
	}

	@Override
	public boolean setDirective(IJp2pDirectives id, String value) {
		if( PeerGroupPropertySource.PeerGroupDirectives.isValidDirective( id.name()))
			return super.setDirective(PeerGroupPropertySource.PeerGroupDirectives.valueOf( id.name()), value );
		return super.setDirective(id, value);
	}

	@Override
	public IJp2pProperties getIdFromString(String key) {
		if( PipeProperties.isValidProperty(key))
			return PipeProperties.valueOf(key);
		return null;
	}

	@Override
	public boolean validate(IJp2pProperties id, Object value) {
		return PipeProperties.isValidProperty(id.toString());	
	}	
}