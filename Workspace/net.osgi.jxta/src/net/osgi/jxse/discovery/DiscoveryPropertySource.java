package net.osgi.jxse.discovery;

import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider.PeerGroupDirectives;
import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.utils.StringStyler;

public class DiscoveryPropertySource extends AbstractJxseWritePropertySource<DiscoveryPropertySource.DiscoveryProperties, IJxseDirectives>
{
	public enum DiscoveryMode{
		DISCOVERY,
		PUBLISH,
		DISCOVERY_AND_PUBLISH;

	@Override
	public String toString() {
		return StringStyler.prettyString( super.toString() );
	}}

	public enum DiscoveryProperties implements IJxseProperties{
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
	
	public DiscoveryPropertySource( IJxsePropertySource<?,IJxseDirectives> parent) {
		this( Components.DISCOVERY_SERVICE.toString(), parent );
	}


	public DiscoveryPropertySource( String componentName, IJxsePropertySource<?,IJxseDirectives> parent) {
		super( componentName,parent );
		this.fillDefaultValues();
	}

	protected void fillDefaultValues() {
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.DISCOVERY_MODE, DiscoveryMode.DISCOVERY, true ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.WAIT_TIME, 60000, true ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.PEER_ID, null, true ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.ATTRIBUTE, null, true ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.WILDCARD, null, true ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.THRESHOLD, 1, true ));
	}


	@Override
	public DiscoveryProperties getIdFromString(String key) {
		return DiscoveryProperties.valueOf( key );
	}

	@Override
	public IJxseDirectives getDirectiveFromString(String id) {
		if( PeerGroupDirectives.isValidIdString(id) )
			return PeerGroupDirectives.valueOf(id);
		return super.getDirectiveFromString(id);
	}

	@Override
	public boolean validate(DiscoveryProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}
