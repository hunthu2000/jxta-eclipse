package net.jp2p.chaupal.jxta.network;

import net.jp2p.container.properties.IPropertyConvertor;
import net.jxta.compatibility.platform.NetworkConfigurator;

public interface INetworkPreferences extends IPropertyConvertor<String, Object>{

	/**
	 * Fill the given configurator with the source
	 * @param configurator
	 * @return
	 */
	boolean fillConfigurator(NetworkConfigurator configurator);
}