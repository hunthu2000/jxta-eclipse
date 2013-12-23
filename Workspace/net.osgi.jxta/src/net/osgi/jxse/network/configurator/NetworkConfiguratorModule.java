package net.osgi.jxse.network.configurator;

import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.network.INetworkManagerProvider;
import net.osgi.jxse.network.NetworkManagerPropertySource;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.seeds.SeedListPropertySource;

public class NetworkConfiguratorModule extends AbstractJxseModule<NetworkConfigurator, NetworkConfigurationPropertySource> {

	private Class<?> clss;
	
	private NetworkManager manager;
	
	public NetworkConfiguratorModule( Class<?> clss, IJxseModule<?> parent) {
		super(parent, 3);
		this.clss = clss;
	}

	@Override
	public String getComponentName() {
		return Components.NETWORK_CONFIGURATOR.toString();
	}

	@Override
	protected NetworkConfigurationPropertySource onCreatePropertySource() {
		NetworkConfigurationPropertySource source = new NetworkConfigurationPropertySource( (NetworkManagerPropertySource) super.getParent().getPropertySource() );
		SeedListPropertySource slps = new SeedListPropertySource( source, clss );
		if( slps.hasSeeds() )
			source.addChild(slps);
		return source;
	}

	@Override
	public IComponentFactory<NetworkConfigurator> onCreateFactory() {
		return new NetworkConfigurationFactory( super.getPropertySource(), manager );
	}

	@Override
	public void notifyCreated(ComponentBuilderEvent<Object> event) {
		if( !event.getBuilderEvent().equals( BuilderEvents.COMPONENT_CREATED ))
			return;
		if( Components.NETWORK_MANAGER.toString().equals( event.getModule().getComponentName() ))
			this.manager = (NetworkManager) event.getModule().getComponent();
		
	}
}
