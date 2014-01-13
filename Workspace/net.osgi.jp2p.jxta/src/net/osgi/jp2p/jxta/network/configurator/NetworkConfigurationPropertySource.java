package net.osgi.jp2p.jxta.network.configurator;

import java.util.Iterator;

import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.jxta.network.NetworkManagerPropertySource;
import net.osgi.jp2p.jxta.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.properties.AbstractJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class NetworkConfigurationPropertySource extends AbstractJp2pWritePropertySource
	implements IJp2pWritePropertySource<IJp2pProperties>

{
	public static String S_NETWORK_CONFIGURATOR = "NetworkConfigurator";
	
	public enum NetworkConfiguratorProperties implements IJp2pProperties{
		DESCRIPTION,
		HOME,
		HTTP_8ENABLED,
		HTTP_8INCOMING_STATUS,
		HTTP_8INTERFACE_ADDRESS,
		HTTP_8OUTGOING_STATUS,
		HTTP_8PORT,
		HTTP_8PUBLIC_ADDRESS,
		HTTP_8TO_PUBLIC_ADDRESS_EXCLUSIVE,
		HTTP_8PUBLIC_ADDRESS_EXCLUSIVE,
		HTTP2_8ENABLED,
		HTTP2_8START_PORT,
		HTTP2_8END_PORT,
		HTTP2_8INCOMING_STATUS,
		HTTP2_8INTERFACE_ADDRESS,
		HTTP2_8OUTGOING_STATUS,
		HTTP2_8PORT,
		HTTP2_8PUBLIC_ADDRESS,
		HTTP2_8TO_PUBLIC_ADDRESS_EXCLUSIVE,
		HTTP2_8PUBLIC_ADDRESS_EXCLUSIVE,
		INFRASTRUCTURE_8NAME,
		INFRASTRUCTURE_8DESCRIPTION,
		INFRASTRUCTURE_8ID,
		MODE,
		MULTICAST_8ENABLED,
		MULTICAST_8ADDRESS,
		MULTICAST_8INTERFACE,
		MULTICAST_8POOL_SIZE,
		MULTICAST_8PORT,
		MULTICAST_8SIZE,
		MULTICAST_8STATUS,
		NAME,
		PEER_ID,
		SECURITY_8AUTHENTICATION_TYPE,
		SECURITY_8CERTFICATE,
		SECURITY_8CERTIFICATE_CHAIN,
		SECURITY_8KEY_STORE_LOCATION,
		SECURITY_8PASSWORD,
		SECURITY_8PRINCIPAL,
		SECURITY_8PRIVATE_KEY,
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

	public NetworkConfigurationPropertySource( NetworkManagerPropertySource nmps ) {
		super( S_NETWORK_CONFIGURATOR, nmps );
		this.fill();
	}

	private void fill(){
		NetworkManagerPropertySource source = (NetworkManagerPropertySource) super.getParent();
		Iterator<IJp2pProperties> iterator = source.propertyIterator();
		NetworkConfiguratorProperties nmp = null;
		while( iterator.hasNext() ){
			IJp2pProperties cp = iterator.next();
			nmp = convertTo( cp );
			if( nmp != null ){
				Object value = source.getProperty( cp );
				super.setProperty(nmp, value, true);
			}
		}
		super.setProperty( NetworkConfiguratorProperties.TCP_8PORT, source.getTcpPort());
		super.setProperty( NetworkConfiguratorProperties.TCP_8ENABLED, true );
		super.setProperty( NetworkConfiguratorProperties.HTTP_8ENABLED, true );
	}

	@Override
	public String getComponentName() {
		return JxtaComponents.NETWORK_CONFIGURATOR.toString();
	}
	
	@Override
	public NetworkConfiguratorProperties getIdFromString(String key) {
		return NetworkConfiguratorProperties.valueOf( key );
	}

	@Override
	public String getIdentifier() {
		NetworkManagerPropertySource source = (NetworkManagerPropertySource) super.getParent();
		if( source != null )
			return source.getIdentifier();
		return null;
	}

	@Override
	public boolean validate(IJp2pProperties id, Object value) {
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
	public static NetworkConfiguratorProperties convertTo( IJp2pProperties props ){
		if(!( props instanceof NetworkManagerProperties ))
			return null;
		NetworkManagerProperties id = (NetworkManagerProperties) props;
		switch( id ){
		case INSTANCE_NAME:
			return NetworkConfiguratorProperties.NAME;			
		case MODE:
			return NetworkConfiguratorProperties.MODE;
		case INSTANCE_HOME:
			return NetworkConfiguratorProperties.STORE_HOME;
		case PEER_ID:
			return NetworkConfiguratorProperties.PEER_ID;
		default:
			break;
		}
		return null;
	}
}
