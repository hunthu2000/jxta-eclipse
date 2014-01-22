package net.jp2p.container.persistence;

import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.properties.AbstractJp2pPropertySource;
import net.jp2p.container.properties.IJp2pDirectives.Directives;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;

public class PersistencePropertySource extends AbstractJp2pPropertySource {

	public PersistencePropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( Jp2pContext.Components.PERSISTENCE_SERVICE.toString(), parent);
		super.setDirective( Directives.BLOCK_CREATION, Boolean.FALSE.toString());
	}
}
