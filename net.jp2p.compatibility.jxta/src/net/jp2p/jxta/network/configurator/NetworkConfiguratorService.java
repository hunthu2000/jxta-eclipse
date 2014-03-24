package net.jp2p.jxta.network.configurator;

import java.net.URI;
import java.net.URL;

import net.jp2p.jxta.module.AbstractModuleComponent;
import net.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.jxta.compatibility.impl.peergroup.Platform;
import net.jxta.compatibility.platform.NetworkConfigurator;
import net.jxta.document.Advertisement;
import net.jxta.impl.protocol.PlatformConfig;
import net.jxta.platform.ModuleClassID;
import net.jxta.protocol.ModuleImplAdvertisement;

public class NetworkConfiguratorService extends AbstractModuleComponent<Platform> {

	public static final String S_RESOURCE_LOCATION = "/Services/net.jxta.platform.Module";

    public NetworkConfiguratorService( NetworkConfigurationPropertySource source, NetworkConfigurator configurator ) {
		super(source, new Platform( configurator.getPlatformConfig(), ( URI)source.getProperty( NetworkConfiguratorProperties.STORE_HOME )));
	}
 
	@Override
	public ModuleClassID getModuleClassID() {
		return null;//TODO
	}


	@Override
	protected ModuleImplAdvertisement onCreateAdvertisement() {
		URL url = NetworkConfiguratorService.class.getResource( S_RESOURCE_LOCATION );
		return getAdvertisementFromResource(url);
	}

	@Override
	public Advertisement getAdvertisement(PlatformConfig config) {
		// TODO Auto-generated method stub
		return null;
	}
}