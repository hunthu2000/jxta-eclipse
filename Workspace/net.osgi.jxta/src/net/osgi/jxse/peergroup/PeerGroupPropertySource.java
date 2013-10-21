package net.osgi.jxse.peergroup;

import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.AbstractPeerGroupProviderPropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;

public class PeerGroupPropertySource extends AbstractPeerGroupProviderPropertySource<PeerGroupPropertySource.PeerGroupProperties>
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
	
	public PeerGroupPropertySource( IJxsePropertySource<?,IJxseDirectives> parent) {
		this( Components.PEERGROUP_SERVICE.toString(), parent );
	}


	public PeerGroupPropertySource( String componentName, IJxsePropertySource<?,IJxseDirectives> parent) {
		super( componentName,parent );
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
	public PeerGroupProperties getIdFromString(String key) {
		return PeerGroupProperties.valueOf( key );
	}


	@Override
	public boolean validate(PeerGroupProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public Object getDefaultDirectives(IJxseDirectives id) {
		// TODO Auto-generated method stub
		return null;
	}
}
