package net.jp2p.chaupal.jxta.network.factory;

import java.util.Collection;

import net.jp2p.chaupal.jxta.module.AbstractModuleFactory;
import net.jp2p.chaupal.jxta.network.Activator;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.jxta.module.IJxtaModuleService;
import net.jxta.compatibility.platform.NetworkConfigurator;
import net.jxta.platform.Module;

public class NetworkModuleFactory extends AbstractModuleFactory<Module> {

	private  NetworkConfigurator configurator;
	
	public NetworkModuleFactory( IJp2pPropertySource<IJp2pProperties> source,  NetworkConfigurator configurator ) {
		super(source, Activator.getModuleFactoryRegistrator() );
		this.configurator = configurator;
    }

	@SuppressWarnings("unchecked")
	@Override
	protected void addModules(
			Collection<IJxtaModuleService<Module>> modules) {
		IJxtaModuleService<? extends Module> service = new PlatformModule( super.getSource(), configurator);
		modules.add( (IJxtaModuleService<Module>) service );
	}
}
