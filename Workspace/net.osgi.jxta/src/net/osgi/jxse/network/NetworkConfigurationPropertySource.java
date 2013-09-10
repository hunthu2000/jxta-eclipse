package net.osgi.jxse.network;

import net.osgi.jxse.context.IJxseServiceContext.ContextDirectives;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.preferences.properties.AbstractJxsePropertySource;
import net.osgi.jxse.preferences.properties.IJxseDirectives;
import net.osgi.jxse.utils.StringStyler;

public class NetworkConfigurationPropertySource extends AbstractJxsePropertySource<NetworkConfigurationPropertySource.NetworkConfiguratorProperties, IJxseDirectives> {


	public enum NetworkConfiguratorProperties{
		AUTHENTICATION_TYPE,
		CERTFICATE,
		CERTIFICATE_CHAIN,
		DESCRIPTION,
		HOME,
		HTTP_8ENABLED,
		HTTP_8START_PORT,
		HTTP_8END_PORT,
		HTTP_8INCOMING_STATUS,
		HTTP_8INTERFACE_ADDRESS,
		HTTP_8OUTGOING_STATUS,
		HTTP_8PORT,
		HTTP_8PUBLIC_ADDRESS,
		HTTP_8TO_PUBLIC_ADDRESS_EXCLUSIVE,
		HTTP_8PUBLIC_ADDRESS_EXCLUSIVE,
		INFRASTRUCTURE_8NAME,
		INFRASTRUCTURE_8DESCRIPTION,
		INFRASTRUCTURE_8ID,
		KEY_STORE_LOCATION,
		MODE,
		MULTICAST_8ADDRESS,
		MULTICAST_8INTERFACE,
		MULTICAST_8POOL_SIZE,
		MULTICAST_8PORT,
		MULTICAST_8SIZE,
		MULTICAST_8STATUS,
		NAME,
		PASSWORD,
		PEER_ID,
		PRINCIPAL,
		PRIVATE_KEY,
		RELAY_8MAX_CLIENTS,
		RELAY_8SEEDING_URIS,
		RELAY_8SEED_URIS,
		RENDEZVOUS_8MAX_CLIENTS,
		RENDEZVOUS_8SEEDING_URIS,
		RENDEZVOUS_8SEED_URIS,
		STORE_HOME,
		TCP_8ENABLED,
		TCP_8END_PORT,
		TCP_8INCOMING_STATUS,
		TCP_8INTERFACE_ADDRESS,
		TCP_8OUTGOING_STATUS,
		TCP_8PORT,
		TCP_8PUBLIC_ADDRESS,
		TCP_8PUBLIC_ADDRESS_EXCLUSIVE,
		TCP_8START_PORT,
		USE_MULTICAST,
		USE_ONLY_RELAY_SEEDS,
		USE_ONLY_RENDEZVOUS_SEEDS;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}	
		
		public static NetworkConfiguratorProperties convertTo( String str ){
			String enumStr = StringStyler.styleToEnum( str );
			return valueOf( enumStr );
		}
	}

	private JxseContextPropertySource source;
	
	public NetworkConfigurationPropertySource( JxseContextPropertySource source) {
		super( source.getBundleId(), source.getIdentifier(), NetworkManagerFactory.S_NETWORK_MANAGER_SERVICE, 2 );
		this.source = source;
	}

	@Override
	public Object getDefaultDirectives(IJxseDirectives id) {
		if( this.source != null )
			return this.source.getDefaultDirectives((ContextDirectives) id);
		return null;
	}

	@Override
	public String getBundleId() {
		if( this.source != null )
			return this.source.getBundleId();
		return null;
	}

	@Override
	public String getIdentifier() {
		if( this.source != null )
			return this.source.getIdentifier();
		return null;
	}

	@Override
	public Object getDefault(NetworkConfiguratorProperties id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validate(NetworkConfiguratorProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

}
