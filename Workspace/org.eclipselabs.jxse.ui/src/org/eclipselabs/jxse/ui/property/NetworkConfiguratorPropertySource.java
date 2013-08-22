package org.eclipselabs.jxse.ui.property;

import java.io.File;
import java.net.URI;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipselabs.jxse.ui.property.descriptors.CheckBoxPropertyDescriptor;
import org.eclipselabs.jxse.ui.property.descriptors.SpinnerPropertyDescriptor;

import net.jxta.id.ID;
import net.jxta.peer.PeerID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.network.NetworkConfigurationFactory.NetworkConfiguratorProperties;
import net.osgi.jxse.utils.EnumUtils;

public class NetworkConfiguratorPropertySource extends AbstractJxsePropertySource<NetworkConfigurator> {

	public static final String S_NO_READ_VALUE = "<Not a readable property>";
	
	public NetworkConfiguratorPropertySource(NetworkConfigurator configurator) {
		super( configurator, new Properties() );
	}

	public NetworkConfiguratorPropertySource(NetworkConfigurator configurator,	Properties defaults) {
		super( configurator, defaults );
	}

	public NetworkConfiguratorPropertySource( IJxseComponent<NetworkConfigurator> component ) {
		super( component );
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		Collection<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
		for( NetworkConfiguratorProperties property: NetworkConfiguratorProperties.values() ){
			String[] parsed = super.parseProperty(property);
			PropertyDescriptor descriptor;
			SpinnerPropertyDescriptor spd;
			switch( property ){
/*
			case CERTFICATE:
				configurator.setCertificate( (X509Certificate) value);
				return;
			case CERTIFICATE_CHAIN:
				configurator.setCertificateChain( (X509Certificate[]) value);
				return;
			case HOME:
				configurator.setHome( (File) value);
				return;
			case HTTP_8PUBLIC_ADDRESS:
				combined = ( Object[] )value;
				return;
			case INFRASTRUCTURE_8ID:
				configurator.setInfrastructureID( (ID) value);
				return;
			case KEY_STORE_LOCATION:
				configurator.setKeyStoreLocation( (URI) value);
				return;
			case PEER_ID:
				configurator.setPeerID( (PeerID) value);
				return;
			case PRIVATE_KEY:
				configurator.setPrivateKey( (PrivateKey) value);
				return;
			case RELAY_8SEED_URIS:
				configurator.setRelaySeedURIs( (List<String>) value);
				return;
			case RELAY_8SEEDING_URIS:
				configurator.setRelaySeedingURIs( (Set<String>) value);
				return;
			case RENDEZVOUS_8SEED_URIS:
				configurator.setRendezvousSeeds( (Set<String>) value);
				return;
			case RENDEZVOUS_8SEEDING_URIS:
				configurator.setRendezvousSeedingURIs( (List<String>) value);
				return;
			case STORE_HOME:
				configurator.setStoreHome( (URI) value);
				return;
			case TCP_8PUBLIC_ADDRESS:
				combined = ( Object[] )value;
				configurator.setTcpPublicAddress( ( String )combined[0], ( boolean)combined[1]);
				return;
				configurator.setHttp2EndPort( (int) value);
				return;
			case TCP_8INTERFACE_ADDRESS:
				configurator.setTcpInterfaceAddress( (String) value);
				return;
				configurator.setTcpOutgoing( (boolean) value);
				return;
*/				
			case MULTICAST_8SIZE:
			case MULTICAST_8POOL_SIZE:
				descriptor = new SpinnerPropertyDescriptor( property, parsed[1], 0, Integer.MAX_VALUE );
				spd = ( SpinnerPropertyDescriptor )descriptor;
				spd.setEnabled( this.isEditable(property));
				break;				
			case RENDEZVOUS_8MAX_CLIENTS:
			case RELAY_8MAX_CLIENTS:
				descriptor = new SpinnerPropertyDescriptor( property, parsed[1], -1, Integer.MAX_VALUE );
				spd = ( SpinnerPropertyDescriptor )descriptor;
				spd.setEnabled( this.isEditable(property));
				break;				
			case MULTICAST_8PORT:
			case HTTP_8PORT:
			case HTTP_8START_PORT:
			case HTTP_8END_PORT:
			case TCP_8START_PORT:
			case TCP_8END_PORT:
			case TCP_8PORT:
				descriptor = new SpinnerPropertyDescriptor( property, parsed[1], 8080, 65535 );
				spd = ( SpinnerPropertyDescriptor )descriptor;
				spd.setEnabled( this.isEditable(property));
				break;				
			case MODE:
				descriptor = new ComboBoxPropertyDescriptor( property, parsed[1], EnumUtils.toString( ConfigMode.values() ));				
				break;
			case HTTP_8ENABLED:
			case HTTP_8INCOMING_STATUS:
			case HTTP_8OUTGOING_STATUS:
			case HTTP_8TO_PUBLIC_ADDRESS_EXCLUSIVE:
			case HTTP_8PUBLIC_ADDRESS_EXCLUSIVE:
			case TCP_8ENABLED:
			case TCP_8INCOMING_STATUS:
			case TCP_8OUTGOING_STATUS:
			case TCP_8PUBLIC_ADDRESS_EXCLUSIVE:
			case USE_MULTICAST:
			case USE_ONLY_RELAY_SEEDS:
			case USE_ONLY_RENDEZVOUS_SEEDS:
				descriptor = new CheckBoxPropertyDescriptor( property, parsed[1] );
				break;
			default:
				descriptor = new TextPropertyDescriptor( property, parsed[1]);
				break;
			}	
			descriptor.setCategory(parsed[2]);
			descriptors.add(descriptor);
		}
		if( super.getPropertyDescriptors() != null )
			descriptors.addAll( Arrays.asList( super.getPropertyDescriptors()));
		return descriptors.toArray( new IPropertyDescriptor[ descriptors.size()]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		NetworkConfigurator configurator = super.getModule();
		NetworkConfiguratorProperties property = ( NetworkConfiguratorProperties )id;
		switch( property ){
		case AUTHENTICATION_TYPE:
			return configurator.getAuthenticationType();
		case CERTFICATE:
			return configurator.getCertificate();
		case CERTIFICATE_CHAIN:
			return configurator.getCertificateChain();
		case DESCRIPTION:
			return S_NO_READ_VALUE;
		case HOME:
			return configurator.getHome();
		case HTTP_8PUBLIC_ADDRESS:
			return configurator.getHttp2PublicAddress();
		case HTTP_8ENABLED:
			return configurator.isHttp2Enabled();
		case HTTP_8END_PORT:
			return configurator.getHttp2EndPort();
		case HTTP_8PUBLIC_ADDRESS_EXCLUSIVE:
			return configurator.isHttpPublicAddressExclusive();
		case HTTP_8INCOMING_STATUS:
			return configurator.getHttp2IncomingStatus();
		case HTTP_8INTERFACE_ADDRESS:
			return configurator.getHttp2InterfaceAddress();
		case HTTP_8OUTGOING_STATUS:
			return configurator.getHttp2OutgoingStatus();
		case HTTP_8PORT:
			return configurator.getHttp2Port();
		case HTTP_8START_PORT:
			return configurator.getHttp2StartPort();
		case HTTP_8TO_PUBLIC_ADDRESS_EXCLUSIVE:
			return configurator.isHttp2PublicAddressExclusive();
		case INFRASTRUCTURE_8DESCRIPTION:
			return configurator.getInfrastructureDescriptionStr();
		case INFRASTRUCTURE_8ID:
			return configurator.getInfrastructureID();
		case INFRASTRUCTURE_8NAME:
			return configurator.getInfrastructureName();
		case KEY_STORE_LOCATION:
			return configurator.getKeyStoreLocation();
		case MODE:
			return configurator.getMode();
		case MULTICAST_8ADDRESS:
			return configurator.getMulticastAddress();
		case MULTICAST_8INTERFACE:
			return configurator.getMulticastInterface();
		case MULTICAST_8POOL_SIZE:
			return configurator.getMulticastPoolSize();
		case MULTICAST_8PORT:
			return configurator.getMulticastPort();
		case MULTICAST_8SIZE:
			return configurator.getMulticastSize();
		case MULTICAST_8STATUS:
			return configurator.getMulticastStatus();
		case NAME:
			return configurator.getName();
		case PASSWORD:
			return configurator.getPassword();
		case PEER_ID:
			return configurator.getPeerID();
		case PRINCIPAL:
			return configurator.getPrincipal();
		case PRIVATE_KEY:
			return configurator.getPrivateKey();
		case RELAY_8MAX_CLIENTS:
			return configurator.getRelayMaxClients();
		case RELAY_8SEED_URIS:
			return configurator.getRelaySeedURIs();
		case RELAY_8SEEDING_URIS:
			return configurator.getRelaySeedingURIs();
		case RENDEZVOUS_8MAX_CLIENTS:
			return configurator.getRendezvousMaxClients();
		case RENDEZVOUS_8SEED_URIS:
			return configurator.getRdvSeedURIs();
		case RENDEZVOUS_8SEEDING_URIS:
			return configurator.getRdvSeedingURIs();
		case STORE_HOME:
			return configurator.getStoreHome();
		case TCP_8PUBLIC_ADDRESS:
			return configurator.getTcpPublicAddress();
		case TCP_8ENABLED:
			return configurator.isTcpEnabled();
		case TCP_8END_PORT:
			return configurator.getTcpEndport();
		case TCP_8PUBLIC_ADDRESS_EXCLUSIVE:
			return configurator.isTcpPublicAddressExclusive();
		case TCP_8INCOMING_STATUS:
			return configurator.getTcpIncomingStatus();
		case TCP_8INTERFACE_ADDRESS:
			return configurator.getTcpInterfaceAddress();
		case TCP_8OUTGOING_STATUS:
			return configurator.getTcpOutgoingStatus();
		case TCP_8PORT:
			return configurator.getTcpPort();
		case TCP_8START_PORT:
			return configurator.getTcpStartPort();
		case USE_MULTICAST:
			return configurator.getMulticastStatus();
		case USE_ONLY_RELAY_SEEDS:
			return configurator.getUseOnlyRelaySeedsStatus();
		case USE_ONLY_RENDEZVOUS_SEEDS:
			return configurator.getUseOnlyRendezvousSeedsStatus();
		}
		return super.getPropertyValue(id);
	}

	/**
	 * Returns true if the given property can be modified
	 * @param id
	 * @return
	 */
	@Override
	public boolean isEditable( Object id ){
		if(!( id instanceof NetworkConfiguratorProperties ))
			return false;
		return this.isEditable( ( NetworkConfiguratorProperties )id);
	}

	/**
	 * Returns true if the given property can be modified
	 * @param id
	 * @return
	 */
	public boolean isEditable( NetworkConfiguratorProperties property ){
		switch( property ){
		case HTTP_8PUBLIC_ADDRESS_EXCLUSIVE:
		case HTTP_8TO_PUBLIC_ADDRESS_EXCLUSIVE:
		case TCP_8PUBLIC_ADDRESS_EXCLUSIVE:
			return false;
		default:
			return true;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPropertyValue(Object id, Object value) {
		if(!( this.isEditable(id)))
			return;
		NetworkConfigurator configurator = super.getModule();
		NetworkConfiguratorProperties property = ( NetworkConfiguratorProperties )id;
		Object[] combined;
		switch( property ){
		case AUTHENTICATION_TYPE:
			configurator.setAuthenticationType( (String) value );
			return;
		case CERTFICATE:
			configurator.setCertificate( (X509Certificate) value);
			return;
		case CERTIFICATE_CHAIN:
			configurator.setCertificateChain( (X509Certificate[]) value);
			return;
		case DESCRIPTION:
			configurator.setDescription((String) value);
			return;
		case HOME:
			configurator.setHome( (File) value);
			return;
		case HTTP_8PUBLIC_ADDRESS:
			combined = ( Object[] )value;
			configurator.setHttp2PublicAddress(( String )combined[0], ( boolean)combined[1] );
			return;
		case HTTP_8ENABLED:
			configurator.setHttp2Enabled( (boolean) value);
			return;
		case HTTP_8END_PORT:
			configurator.setHttp2EndPort( (int) value);
			return;
		case HTTP_8PUBLIC_ADDRESS_EXCLUSIVE:
			//configurator.setHttpPublicAddressExclusive(( String )combined[0], ( boolean )combined[1]);
			return;
		case HTTP_8INCOMING_STATUS:
			configurator.setHttp2Incoming( (boolean) value);
			return;
		case HTTP_8INTERFACE_ADDRESS:
			configurator.setHttp2InterfaceAddress( (String) value);
			return;
		case HTTP_8OUTGOING_STATUS:
			configurator.setHttp2Outgoing( (boolean) value);
			return;
		case HTTP_8PORT:
			configurator.setHttp2Port( (int) value);
			return;
		case HTTP_8START_PORT:
			configurator.setHttp2StartPort( (int) value);
			return;
		case HTTP_8TO_PUBLIC_ADDRESS_EXCLUSIVE:
			//configurator.setHttp2PublicAddressExclusive( value);
			return;
		case INFRASTRUCTURE_8DESCRIPTION:
			configurator.setInfrastructureDescriptionStr( (String) value);
			return;
		case INFRASTRUCTURE_8ID:
			configurator.setInfrastructureID( (ID) value);
			return;
		case INFRASTRUCTURE_8NAME:
			configurator.setInfrastructureName( (String) value);
			return;
		case KEY_STORE_LOCATION:
			configurator.setKeyStoreLocation( (URI) value);
			return;
		case MODE:
			configurator.setMode( (int) value);
			return;
		case MULTICAST_8ADDRESS:
			configurator.setMulticastAddress( (String) value);
			return;
		case MULTICAST_8INTERFACE:
			configurator.setMulticastInterface( (String) value);
			return;
		case MULTICAST_8POOL_SIZE:
			configurator.setMulticastPoolSize( (int) value);
			return;
		case MULTICAST_8PORT:
			configurator.setMulticastPort( (int) value);
			return;
		case MULTICAST_8SIZE:
			configurator.setMulticastSize( (int) value);
			return;
		case MULTICAST_8STATUS:
			configurator.setMulticastInterface( (String) value);
			return;
		case NAME:
			configurator.setName( (String) value);
			return;
		case PASSWORD:
			configurator.setPassword( (String) value);
			return;
		case PEER_ID:
			configurator.setPeerID( (PeerID) value);
			return;
		case PRINCIPAL:
			configurator.setPrincipal( (String) value);
			return;
		case PRIVATE_KEY:
			configurator.setPrivateKey( (PrivateKey) value);
			return;
		case RELAY_8MAX_CLIENTS:
			configurator.setRelayMaxClients( (int) value);
			return;
		case RELAY_8SEED_URIS:
			configurator.setRelaySeedURIs( (List<String>) value);
			return;
		case RELAY_8SEEDING_URIS:
			configurator.setRelaySeedingURIs( (Set<String>) value);
			return;
		case RENDEZVOUS_8MAX_CLIENTS:
			configurator.setRendezvousMaxClients( (int) value);
			return;
		case RENDEZVOUS_8SEED_URIS:
			configurator.setRendezvousSeeds( (Set<String>) value);
			return;
		case RENDEZVOUS_8SEEDING_URIS:
			configurator.setRendezvousSeedingURIs( (List<String>) value);
			return;
		case STORE_HOME:
			configurator.setStoreHome( (URI) value);
			return;
		case TCP_8PUBLIC_ADDRESS:
			combined = ( Object[] )value;
			configurator.setTcpPublicAddress( ( String )combined[0], ( boolean)combined[1]);
			return;
		case TCP_8ENABLED:
			configurator.setTcpEnabled( (boolean) value);
			return;
		case TCP_8END_PORT:
			configurator.setHttp2EndPort( (int) value);
			return;
		case TCP_8PUBLIC_ADDRESS_EXCLUSIVE:
			//configurator.setTcpPublicAddressExclusive( value);
			return;
		case TCP_8INCOMING_STATUS:
			configurator.setTcpIncoming( (boolean) value);
			return;
		case TCP_8INTERFACE_ADDRESS:
			configurator.setTcpInterfaceAddress( (String) value);
			return;
		case TCP_8OUTGOING_STATUS:
			configurator.setTcpOutgoing( (boolean) value);
			return;
		case TCP_8PORT:
			configurator.setTcpPort( (int) value);
			return;
		case TCP_8START_PORT:
			configurator.setTcpStartPort( (int) value);
			return;
		case USE_MULTICAST:
			configurator.setUseMulticast( (boolean) value );
			return;
		case USE_ONLY_RELAY_SEEDS:
			configurator.setUseOnlyRelaySeeds( (boolean) value);
			return;
		case USE_ONLY_RENDEZVOUS_SEEDS:
			configurator.setUseOnlyRendezvousSeeds( (boolean) value);
			return;
		}
		super.setPropertyValue(id, value);
	}
}