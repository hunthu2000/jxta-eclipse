package net.osgi.jxse.network;

import java.util.Iterator;

import net.osgi.jxse.context.IJxseServiceContext.ContextDirectives;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jxse.properties.AbstractJxsePropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.utils.StringStyler;

public class NetworkConfigurationPropertySource extends AbstractJxsePropertySource<NetworkConfigurationPropertySource.NetworkConfiguratorProperties, IJxseDirectives>

{
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

	private NetworkManagerPropertySource source;
	
	public NetworkConfigurationPropertySource( NetworkManagerFactory factory ) {
		this( (NetworkManagerPropertySource) factory.getPropertySource() );
		this.fill( source );
	}

	public NetworkConfigurationPropertySource( NetworkManagerPropertySource nmps ) {
		super( nmps.getBundleId(), nmps.getIdentifier(), Components.NETWORK_CONFIGURATOR.name(), 2 );
		source =  nmps;
		this.fill( source );
	}

	private void fill( NetworkManagerPropertySource source ){
		Iterator<NetworkManagerProperties> iterator = source.propertyIterator();
		while( iterator.hasNext() ){
			NetworkManagerProperties cp = iterator.next();
			NetworkConfiguratorProperties nmp = convertTo( cp );
			if( nmp == null )
				continue;
			Object value = source.getProperty( cp );
			super.setProperty(nmp, value);
		}
		super.setProperty( NetworkConfiguratorProperties.TCP_8PORT, source.getTcpPort());
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
	public boolean validate(NetworkConfiguratorProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * convert the given context property to a networkManagerProperty, or null if there is
	 * no relation between them
	 * @param context
	 * @return
	 */
	public static NetworkManagerProperties convertFrom( NetworkConfiguratorProperties context ){
		switch( context ){
		case NAME:
			return NetworkManagerProperties.INSTANCE_NAME;
		case MODE:
			return NetworkManagerProperties.MODE;
		case HOME:
			return NetworkManagerProperties.INSTANCE_HOME;
		case PEER_ID:
			return NetworkManagerProperties.PEER_ID;
		default:
			break;
		}
		return null;
	}

	/**
	 * convert the given context property to a networkManagerProperty, or null if there is
	 * no relation between them
	 * @param context
	 * @return
	 */
	public static NetworkConfiguratorProperties convertTo( NetworkManagerProperties props ){
		switch( props ){
		case INSTANCE_NAME:
			return NetworkConfiguratorProperties.NAME;			
		case MODE:
			return NetworkConfiguratorProperties.MODE;
		case INSTANCE_HOME:
			return NetworkConfiguratorProperties.HOME;
		case PEER_ID:
			return NetworkConfiguratorProperties.PEER_ID;
		default:
			break;
		}
		return null;
	}

}
