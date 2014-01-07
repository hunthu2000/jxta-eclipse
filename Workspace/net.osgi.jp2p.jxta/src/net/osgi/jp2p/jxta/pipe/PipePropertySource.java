package net.osgi.jp2p.jxta.pipe;

import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public class PipePropertySource extends AdvertisementPropertySource{

	public static final long DEFAULT_OUTPUT_PIPE_TIME_OUT = 5000;
	
	/**
	 * Properties specific for pipe services
	 * @author Kees
	 *
	 */
	public enum PipeProperties implements IJp2pProperties{
		PIPE_ID,
		TIME_OUT,
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
		super( JxtaComponents.PIPE_SERVICE.toString(), parent);
		super.setProperty( PipeProperties.TIME_OUT, DEFAULT_OUTPUT_PIPE_TIME_OUT);
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
		return super.getIdFromString(key);
	}

	@Override
	public boolean validate(IJp2pProperties id, Object value) {
		return PipeProperties.isValidProperty(id.toString());	
	}	
}