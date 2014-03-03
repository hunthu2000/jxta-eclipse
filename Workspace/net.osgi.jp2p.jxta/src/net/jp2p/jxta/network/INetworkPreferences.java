package net.jp2p.jxta.network;

import net.jp2p.container.properties.IJp2pProperties;
import net.jxta.compatibility.platform.NetworkConfigurator;

public interface INetworkPreferences {

	/**
	 * Get the name of the preference store
	 * @return
	 */
	public String getName();
	
	/**
	 * Fill the given configurator with the source
	 * @param configurator
	 * @return
	 */
	boolean fillConfigurator(NetworkConfigurator configurator);

	/* (non-Javadoc)
	 * @see net.osgi.jxse.network.INetworkPreferences#setPropertyFromString(net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties, java.lang.String)
	 */
	public Object convertTo( IJp2pProperties id, String value );
}