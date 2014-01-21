package net.osgi.jp2p.persistence;

import net.osgi.jp2p.factory.IComponentFactory.Components;
import net.osgi.jp2p.properties.AbstractJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public class PersistencePropertySource extends AbstractJp2pPropertySource {

	protected PersistencePropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( Components.PERSISTENCE_SERVICE.toString(), parent);
	}
}
