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
import net.osgi.jxse.seeds.SeedListPropertySource;

public class NetworkConfiguratorModule extends AbstractJxseModule<NetworkConfigurator, NetworkConfigurationPropertySource> {

	private Class<?> clss;
	
	public NetworkConfiguratorModule( Class<?> clss) {
		this.clss = clss;
	}

	public NetworkConfiguratorModule( Class<?> clss, IJxsePropertySource<?> parent) {
		super(parent);
		this.clss = clss;
	}

	@Override
	public String getComponentName() {
		return Components.NETWORK_CONFIGURATOR.toString();
	}

	@Override
	protected NetworkConfigurationPropertySource onCreatePropertySource() {
		NetworkConfigurationPropertySource source = new NetworkConfigurationPropertySource( (NetworkManagerPropertySource) super.getParent() );
		SeedListPropertySource slps = new SeedListPropertySource( source, clss );
		if( slps.hasSeeds() )
			source.addChild(slps);
		return source;
	}

	@Override
	public IComponentFactory<NetworkConfigurator, IJxseProperties> createFactory( IPeerGroupProvider provider ) {
		return new NetworkConfigurationFactory( (INetworkManagerProvider) provider, super.getPropertySource() );
	}
}
