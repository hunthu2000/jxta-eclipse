package net.osgi.jxse.network;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.component.AbstractJxseModule;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class NetworkManagerModule extends AbstractJxseModule<NetworkManager, NetworkManagerPropertySource> {

	public NetworkManagerModule(IJxsePropertySource<?> parent) {
		super(parent);
	}

	@Override
	public String getComponentName() {
		return Components.STARTUP_SERVICE.toString();
	}

	@Override
	protected NetworkManagerPropertySource onCreatePropertySource() {
		return new NetworkManagerPropertySource( (JxseContextPropertySource) super.getParent());
	}

	@Override
	public IComponentFactory<NetworkManager, IJxseProperties> createFactory( IPeerGroupProvider provider ) {
		return new NetworkManagerFactory( super.getPropertySource() );
	}
}
