package net.osgi.jp2p.persistence;

import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.factory.AbstractFilterFactory;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.filter.AbstractBuilderEventFilter;
import net.osgi.jp2p.filter.IComponentFactoryFilter;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.IManagedPropertyListener;
import net.osgi.jp2p.properties.IPropertyEventDispatcher;

public class PersistenceFactory extends AbstractFilterFactory<IManagedPropertyListener<IJp2pProperties, Object>> {

	private PersistencePropertySource source;
	private PersistenceService service;
	
	@SuppressWarnings("unchecked")
	public PersistenceFactory(IContainerBuilder container,
			IJp2pPropertySource<IJp2pProperties> parentSource, IPersistedProperty<?> property ) {
		super(container, parentSource);
		source = new PersistencePropertySource( parentSource );
		property.setPropertySource((IJp2pWritePropertySource<IJp2pProperties>) source);
		service = new PersistenceService( (IJp2pWritePropertySource<IJp2pProperties>) source, property );
		service.start();
	}

	@Override
	protected IJp2pPropertySource<IJp2pProperties> onCreatePropertySource() {
		return source;
	}

	@Override
	protected IComponentFactoryFilter createFilter() {
		IComponentFactoryFilter filter = new AbstractBuilderEventFilter<IJp2pComponent<IManagedPropertyListener<IJp2pProperties, Object>>>( BuilderEvents.PROPERTY_SOURCE_CREATED, this ){

			@Override
			public boolean accept(ComponentBuilderEvent<?> event) {
				if(!( event.getFactory().getPropertySource() instanceof IPropertyEventDispatcher))
					return false;
				IPropertyEventDispatcher dispatcher = (IPropertyEventDispatcher) event.getFactory().getPropertySource();
				service.addDispatcher(dispatcher);
				return false;
			}

			//Accept all property source created events
			@Override
			protected boolean onCorrectBuilderEvent(ComponentBuilderEvent<?> event) {
				return true;
			}
		};
		return filter;
	}

	@Override
	protected IJp2pComponent<IManagedPropertyListener<IJp2pProperties, Object>> onCreateComponent(
			IJp2pPropertySource<IJp2pProperties> properties) {
		return service;
	}

}
