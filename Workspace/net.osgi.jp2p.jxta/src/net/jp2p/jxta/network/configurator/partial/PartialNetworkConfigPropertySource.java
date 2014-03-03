package net.jp2p.jxta.network.configurator.partial;

import net.jp2p.container.partial.PartialPropertySource;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.utils.StringStyler;
import net.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;

/**
 * A partial property source breaks a parent up in distinct parts, based on the existence of dots (_8) in the given properties
 * @author Kees
 *
 * @param <IJp2pComponents>
 * @param <U>
 */
public class PartialNetworkConfigPropertySource extends PartialPropertySource{

	public PartialNetworkConfigPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( parent );
	}

	public PartialNetworkConfigPropertySource( String componentName,  IJp2pPropertySource<IJp2pProperties> parent) {
		super( componentName, parent );
	}

	@Override
	public IJp2pProperties getIdFromString(String key) {
		String cat = this.getCategory().toLowerCase();
		String id = StringStyler.styleToEnum( cat + "." + key.toLowerCase() );
		return (IJp2pProperties) NetworkConfiguratorProperties.valueOf( id );
	}
}