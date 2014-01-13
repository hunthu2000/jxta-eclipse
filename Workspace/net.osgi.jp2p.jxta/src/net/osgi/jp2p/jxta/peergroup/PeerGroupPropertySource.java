package net.osgi.jp2p.jxta.peergroup;

import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.properties.AbstractPeerGroupProviderPropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;

public class PeerGroupPropertySource extends AbstractPeerGroupProviderPropertySource<IJp2pProperties>
{
	public static final String S_NET_PEER_GROUP = "NetPeerGroup";

	public enum PeerGroupProperties implements IJp2pProperties{
		PEER_ID,
		STORE_HOME,
		PEER_NAME,
		PEERGROUP_NAME,
		PEERGROUP_ID;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public PeerGroupPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		this( JxtaComponents.PEERGROUP_SERVICE.toString(), parent );
		setDirectiveFromParent( Directives.AUTO_START, this );
	}

	public PeerGroupPropertySource( String componentName, IJp2pPropertySource<IJp2pProperties> parent) {
		super( componentName,parent );
		setDirectiveFromParent( Directives.AUTO_START, this );
	}

	@Override
	public String getIdentifier() {
		return super.getIdentifier();
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