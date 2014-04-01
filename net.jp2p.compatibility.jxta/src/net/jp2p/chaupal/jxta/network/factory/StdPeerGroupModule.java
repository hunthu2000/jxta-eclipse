package net.jp2p.chaupal.jxta.network.factory;

import java.net.URI;

import net.jp2p.chaupal.jxta.root.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.jxta.module.AbstractModuleComponent;
import net.jxta.compatibility.platform.NetworkConfigurator;
import net.jxta.document.Advertisement;
import net.jxta.impl.peergroup.Platform;
import net.jxta.impl.peergroup.StdPeerGroup;
import net.jxta.impl.protocol.PlatformConfig;
import net.jxta.platform.ModuleClassID;
import net.jxta.protocol.ModuleImplAdvertisement;

public class StdPeerGroupModule extends AbstractModuleComponent<StdPeerGroup> {

    public StdPeerGroupModule( IJp2pPropertySource<IJp2pProperties> source, NetworkConfigurator configurator ) {
		super(source, new Platform( configurator.getPlatformConfig(), ( URI)source.getProperty( NetworkConfiguratorProperties.STORE_HOME )));
	}
 
	@Override
	public ModuleClassID getModuleClassID() {
		return null;//TODO
	}


	@Override
	protected ModuleImplAdvertisement onCreateAdvertisement() {
		return Platform.getDefaultModuleImplAdvertisement();
	}

	@Override
	public Advertisement getAdvertisement(PlatformConfig config) {
		// TODO Auto-generated method stub
		return null;
	}
}