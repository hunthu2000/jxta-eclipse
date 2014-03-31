package net.jp2p.chaupal.jxta.network.factory;

import java.net.URL;

import net.jp2p.chaupal.jxta.root.network.configurator.NetworkConfigurationPropertySource;
import net.jp2p.jxta.module.AbstractModuleComponent;
import net.jxta.compatibility.peergroup.WorldPeerGroupFactory;
import net.jxta.compatibility.platform.NetworkConfigurator;
import net.jxta.document.Advertisement;
import net.jxta.impl.protocol.PlatformConfig;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.ModuleClassID;
import net.jxta.protocol.ModuleImplAdvertisement;

public class WorldPeerGroupModule extends AbstractModuleComponent<PeerGroup> {

    public WorldPeerGroupModule( NetworkConfigurationPropertySource source, NetworkConfigurator configurator ) {
		super(source, getWorldPeerGroup() );
	}
 
	@Override
	public ModuleClassID getModuleClassID() {
		return null;//TODO
	}


	@Override
	protected ModuleImplAdvertisement onCreateAdvertisement() {
		URL url = WorldPeerGroupModule.class.getResource( S_RESOURCE_LOCATION );
		return getAdvertisementFromResource(url);
	}

	@Override
	public Advertisement getAdvertisement(PlatformConfig config) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static PeerGroup getWorldPeerGroup(){
		try{
			return new WorldPeerGroupFactory().getWorldPeerGroup();
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return null;
	}
}