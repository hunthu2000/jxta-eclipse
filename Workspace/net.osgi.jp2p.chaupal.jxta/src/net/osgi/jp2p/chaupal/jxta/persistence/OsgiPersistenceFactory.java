package net.osgi.jp2p.chaupal.jxta.persistence;

import org.eclipse.core.runtime.preferences.ConfigurationScope;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.context.IJp2pContext;
import net.jp2p.container.persistence.IPersistedProperties;
import net.jp2p.container.persistence.PersistenceService;
import net.jp2p.container.persistence.SimplePersistenceFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IManagedPropertyListener;
import net.osgi.jp2p.chaupal.jxta.context.ChaupalContext;
import net.osgi.jp2p.chaupal.persistence.PersistedProperties;

public class OsgiPersistenceFactory extends SimplePersistenceFactory{
	
	public OsgiPersistenceFactory(IContainerBuilder container,
			IJp2pPropertySource<IJp2pProperties> parentSource ) {
		super(container, parentSource);
	}

	@Override
	protected IJp2pComponent<IManagedPropertyListener<IJp2pProperties, Object>> onCreateComponent(
			IJp2pPropertySource<IJp2pProperties> source) {
		IJp2pContext<?> context = new ChaupalContext();
		IPersistedProperties<String> properties = new PersistedProperties( (IJp2pWritePropertySource<IJp2pProperties>) super.getPropertySource(), ConfigurationScope.INSTANCE );
		PersistenceService<String,Object> service = new PersistenceService<String,Object>( (IJp2pWritePropertySource<IJp2pProperties>) source, properties, context );
		service.start();
		return service;
	}

}
