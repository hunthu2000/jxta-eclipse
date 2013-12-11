package net.osgi.jxse.discovery;

import net.osgi.jxse.advertisement.AdvertisementPropertySource;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.AbstractPeerGroupProviderPropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.utils.StringStyler;

public class DiscoveryPropertySource extends AbstractPeerGroupProviderPropertySource<DiscoveryPropertySource.DiscoveryProperties>
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

	public enum DiscoveryProperties implements IJxseProperties{
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

	public DiscoveryPropertySource( IJxsePropertySource<?,IJxseDirectives> parent) {
		this( Components.DISCOVERY_SERVICE.toString(), parent );
	}


	public DiscoveryPropertySource( String componentName, IJxsePropertySource<?,IJxseDirectives> parent) {
		super( componentName,parent );
		this.fillDefaultValues();
		this.fillDefaultValues(parent);
	}

	protected void fillDefaultValues( IJxsePropertySource<?,IJxseDirectives> parent) {
		if(!( parent instanceof AdvertisementPropertySource ))
			return;
		AdvertisementPropertySource aps = (AdvertisementPropertySource) parent;
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.ATTRIBUTE, S_NAME, false ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.WILDCARD, aps.getIdentifier() ));
	}

	protected void fillDefaultValues( ) {
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.MODE, DiscoveryMode.COUNT, false ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.COUNT, 20, false ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.COUNTER, 20, S_RUNTIME, false ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.WAIT_TIME, 20000, false ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.PEER_ID, null, false ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.ATTRIBUTE, null, false ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.WILDCARD, null, false ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.ADVERTISEMENT_TYPE, AdvertisementTypes.ADV, false ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.THRESHOLD, 1, false ));
		this.setManagedProperty( new ManagedProperty<DiscoveryProperties, Object>( DiscoveryProperties.FOUND, 0, false ));
	}

	@Override
	public DiscoveryProperties getIdFromString(String key) {
		return DiscoveryProperties.valueOf( key );
	}

	@Override
	public boolean validate(DiscoveryProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}