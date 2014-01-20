package net.osgi.jp2p.jxta.peergroup;

import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class PeerGroupPropertySource extends AdvertisementPropertySource
{
	public static final String S_NET_PEER_GROUP = "NetPeerGroup";

	public enum PeerGroupProperties implements IJp2pProperties{
		NAME,
		DESCRIPTION,
		GROUP_ID,
		PEER_ID,
		STORE_HOME,
		PEER_NAME,
		PEERGROUP_ID;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public enum PeerGroupDirectives implements IJp2pDirectives{
		TYPE,
		PEERGROUP,
		PUBLISH;
	
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

	public PeerGroupPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		this( JxtaComponents.PEERGROUP_SERVICE.toString(), parent );
		this.fillDefaultValues();
	}

	public PeerGroupPropertySource( String componentName, IJp2pPropertySource<IJp2pProperties> parent) {
		super( componentName,parent );
		this.fillDefaultValues();
	}

	protected void fillDefaultValues() {
		setDirectiveFromParent( Directives.AUTO_START, this );
		super.setDirective( Directives.BLOCK_CREATION, Boolean.FALSE.toString() );
		IJp2pWritePropertySource<IJp2pProperties> parent = (IJp2pWritePropertySource<IJp2pProperties>) super.getParent();
		this.setDirective( Directives.NAME, parent.getDirective( Directives.NAME ));
		String name = (String) super.getProperty( PeerGroupProperties.NAME );
		if( Utils.isNull( name ))
			name = (String) super.getDirective( IJp2pDirectives.Directives.NAME );
		if(!Utils.isNull( name ))
			super.setProperty( PeerGroupProperties.NAME, name );
	}

	@Override
	public boolean setDirective(IJp2pDirectives id, String value) {
		if( PeerGroupDirectives.isValidDirective( id.name()))
			return super.setDirective(PeerGroupDirectives.valueOf( id.name()), value );
		return super.setDirective(id, value);
	}

	@Override
	public PeerGroupProperties getIdFromString(String key) {
		return PeerGroupProperties.valueOf( key );
	}
}