package net.osgi.jp2p.persistence;

import net.osgi.jp2p.context.Jp2pContext;
import net.osgi.jp2p.context.Jp2pContext.Components;
import net.osgi.jp2p.properties.AbstractJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public class PersistencePropertySource extends AbstractJp2pPropertySource {

	protected PersistencePropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( Jp2pContext.Components.PERSISTENCE_SERVICE.toString(), parent);
	}
}
