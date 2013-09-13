package net.osgi.jxse.network;

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.peer.PeerID;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.properties.IJxseDirectives;

public interface INetworkManagerPropertySource<T extends IJxseDirectives> {

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getConfigMode()
	 */
	public abstract ConfigMode getConfigMode();

	public abstract void setConfigMode(ConfigMode mode);

	public abstract void setConfigMode(String mode);

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getHomeFolder()
	 */
	public abstract URI getHomeFolder() throws URISyntaxException;

	public abstract void setHomeFolder(URI homeFolder);

	public abstract void setHomeFolder(String homeFolder);

	/* (non-Javadoc)
	 * @see net.osgi.jxta.preferences.IJxtaPreferences#getPeerID()
	 */
	public abstract PeerID getPeerID() throws URISyntaxException;

	public abstract void setPeerID(PeerID peerID);

	public abstract String getInstanceName();

	public abstract void setInstanceName(String name);

}