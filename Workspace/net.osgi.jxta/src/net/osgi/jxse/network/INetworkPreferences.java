package net.osgi.jxse.network;

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;

public interface INetworkPreferences {

	/**
	 * Get the name of the preference store
	 * @return
	 */
	public String getName();
	
	public abstract void setPropertyFromString(
			NetworkConfiguratorProperties id, String value);

	/**
	 * Fill the given configurator with the source
	 * @param configurator
	 * @return
	 */
	boolean fillConfigurator(NetworkConfigurator configurator);

}