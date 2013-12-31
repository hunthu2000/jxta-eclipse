package net.osgi.jp2p.discovery;

import net.osgi.jp2p.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.factory.IComponentFactory.Components;
import net.osgi.jp2p.properties.AbstractPeerGroupProviderPropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.utils.StringStyler;

public class DiscoveryPropertySource extends AbstractPeerGroupProviderPropertySource<IJp2pProperties>
{
	public static final String S_NAME = "Name";
	
	public enum DiscoveryMode{
		ONE_SHOT,
		CONTINUOUS,
		COUNT;

	@Override
	public String toString() {
		return StringStyler.prettyString( super.toString() );
	}}

	public enum DiscoveryProperties implements IJp2pProperties{
		MODE,
		WAIT_TIME,
		PEER_ID,
		ATTRIBUTE,
		WILDCARD,
		ADVERTISEMENT_TYPE,
		COUNT,
		COUNTER,
		FOUND,
		THRESHOLD;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public DiscoveryPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		this( Components.DISCOVERY_SERVICE.toString(), parent );
	}


	public DiscoveryPropertySource( String componentName, IJp2pPropertySource<IJp2pProperties> parent) {
		super( componentName, parent );
		this.fillDefaultValues();
		this.fillDefaultValues(parent);
	}

	protected void fillDefaultValues( IJp2pPropertySource<?> parent) {
		setDirectiveFromParent( PeerGroupDirectives.PEERGROUP, this );

		if(!( parent instanceof AdvertisementPropertySource ))
			return;
		AdvertisementPropertySource aps = (AdvertisementPropertySource) parent;
		//this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( DiscoveryProperties.ATTRIBUTE, S_NAME, false ));
		//this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( DiscoveryProperties.WILDCARD, aps.getIdentifier() ));
	}

	protected void fillDefaultValues( ) {
		this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( DiscoveryProperties.MODE, DiscoveryMode.COUNT, false ));
		this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( DiscoveryProperties.COUNT, 20, false ));
		this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( DiscoveryProperties.WAIT_TIME, 20000, false ));
		this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( DiscoveryProperties.PEER_ID, null, false ));
		this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( DiscoveryProperties.ATTRIBUTE, null, false ));
		this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( DiscoveryProperties.WILDCARD, null, false ));
		this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( DiscoveryProperties.ADVERTISEMENT_TYPE, AdvertisementTypes.ADV, false ));
		this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( DiscoveryProperties.THRESHOLD, 1, false ));
		this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( DiscoveryProperties.COUNTER, 20, S_RUNTIME, false ));
		this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( DiscoveryProperties.FOUND, 0, S_RUNTIME, false ));
	}

	@Override
	public boolean setDirective(IJp2pDirectives id, String value) {
		if( AdvertisementPropertySource.AdvertisementDirectives.isValidDirective( id.name()))
			return super.setDirective(PeerGroupDirectives.valueOf( id.name()), value );
		return super.setDirective(id, value);
	}
}