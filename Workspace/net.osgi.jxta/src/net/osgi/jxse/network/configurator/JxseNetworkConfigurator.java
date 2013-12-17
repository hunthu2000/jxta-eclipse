package net.osgi.jxse.network.configurator;

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jxse.component.AbstractJxseModule;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.network.INetworkManagerProvider;
import net.osgi.jxse.network.NetworkManagerPropertySource;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class JxseNetworkConfigurator extends AbstractJxseModule<NetworkConfigurator, NetworkConfigurationPropertySource> {

	public JxseNetworkConfigurator(IJxsePropertySource<?> parent) {
		super(parent);
	}

	@Override
	public String getComponentName() {
		return Components.NETWORK_CONFIGURATOR.toString();
	}

	@Override
	protected NetworkConfigurationPropertySource onCreatePropertySource() {
		return new NetworkConfigurationPropertySource((NetworkManagerPropertySource) super.getParent());
	}

	@Override
	public IComponentFactory<NetworkConfigurator, IJxseProperties> createFactory( IPeerGroupProvider provider ) {
		return new NetworkConfigurationFactory( (INetworkManagerProvider) provider, super.getPropertySource() );
	}
}
