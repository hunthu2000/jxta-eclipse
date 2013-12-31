package net.osgi.jp2p.network;

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jp2p.properties.IJp2pProperties;

public interface INetworkPreferences {

	/**
	 * Get the name of the preference store
	 * @return
	 */
	public String getName();
	
	public abstract boolean setPropertyFromString(IJp2pProperties id, String value);

	/**
	 * Fill the given configurator with the source
	 * @param configurator
	 * @return
	 */
	boolean fillConfigurator(NetworkConfigurator configurator);

}