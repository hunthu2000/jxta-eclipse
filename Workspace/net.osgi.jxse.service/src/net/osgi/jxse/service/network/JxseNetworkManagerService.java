package net.osgi.jxse.service.network;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.component.DefaultJxseComponent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.network.NetworkManagerFactory;
import net.osgi.jxse.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.osgi.jxse.properties.IJxseDirectives;

public class JxseNetworkManagerService extends DefaultJxseComponent<NetworkManager, NetworkManagerProperties, IJxseDirectives>{

	public JxseNetworkManagerService( IComponentFactory<?, ?, IJxseDirectives> parent, NetworkManagerFactory factory) {
		super(factory );
	}
}
