package net.osgi.jxse.service.network;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.component.DefaultJxseComponent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.network.NetworkManagerFactory;

public class JxseNetworkManagerService extends DefaultJxseComponent<NetworkManager>{

	public JxseNetworkManagerService( IComponentFactory<?> parent, NetworkManagerFactory factory) {
		super(factory );
	}
}
