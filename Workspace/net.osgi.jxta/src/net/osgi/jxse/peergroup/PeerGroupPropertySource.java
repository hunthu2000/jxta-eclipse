package net.osgi.jxse.peergroup;

import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.PeerGroupPropertySource;
import net.osgi.jxse.properties.AbstractPeerGroupProviderPropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.utils.StringStyler;

public class PeerGroupPropertySource extends AbstractPeerGroupProviderPropertySource<IJxseProperties>
{
	public enum PeerGroupProperties implements IJxseProperties{
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
	
	public PeerGroupPropertySource( IJxsePropertySource<IJxseProperties> parent) {
		this( Components.PEERGROUP_SERVICE.toString(), parent );
		setDirectiveFromParent( Directives.AUTO_START, this );
	}


	protected PeerGroupPropertySource( String componentName, IJxsePropertySource<IJxseProperties> parent) {
		super( componentName,parent );
		setDirectiveFromParent( Directives.AUTO_START, this );
		this.fillDefaultValues();
	}

	protected void fillDefaultValues() {
		//this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.DISCOVERY_MODE, DiscoveryMode.DISCOVERY, true ));
		//this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.WAIT_TIME, 60000, true ));
		//this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.PEER_ID, null, true ));
		//this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.ATTRIBUTE, null, true ));
		//this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.WILDCARD, null, true ));
		//this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.THRESHOLD, 1, true ));
	}

	@Override
	public boolean setDirective(IJxseDirectives id, String value) {
		if( PeerGroupDirectives.isValidDirective( id.name()))
			return super.setDirective(PeerGroupDirectives.valueOf( id.name()), value );
		return super.setDirective(id, value);
	}

	@Override
	public PeerGroupProperties getIdFromString(String key) {
		return PeerGroupProperties.valueOf( key );
	}
}
