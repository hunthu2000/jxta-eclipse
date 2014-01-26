package net.jp2p.container.persistence;

import java.util.Iterator;
import java.util.Stack;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.context.IJp2pContext;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.factory.AbstractComponentFactory;
import net.jp2p.container.factory.ComponentBuilderEvent;
import net.jp2p.container.factory.filter.BuilderEventFilter;
import net.jp2p.container.factory.filter.IComponentFactoryFilter;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IManagedPropertyListener;
import net.jp2p.container.properties.IPropertyConvertor;
import net.jp2p.container.properties.IPropertyEventDispatcher;
import net.jp2p.container.properties.IJp2pDirectives.Directives;
import net.jp2p.container.properties.ManagedProperty;

public class SimplePersistenceFactory extends AbstractComponentFactory<IManagedPropertyListener<IJp2pProperties, Object>> {

	private Stack<IPropertyEventDispatcher> stack;
	
	private IComponentFactoryFilter filter = new BuilderEventFilter<IJp2pComponent<IManagedPropertyListener<IJp2pProperties, Object>>>( BuilderEvents.PROPERTY_SOURCE_PREPARED, this );

	
	public SimplePersistenceFactory(IContainerBuilder builder,
			IJp2pPropertySource<IJp2pProperties> parentSource ) {
		super(builder, parentSource);
		stack = new Stack<IPropertyEventDispatcher>();
	}

	@Override
	protected IJp2pPropertySource<IJp2pProperties> onCreatePropertySource() {
		IJp2pPropertySource<IJp2pProperties> source = new PersistencePropertySource( super.getParentSource() );
		return source;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		if( !filter.accept(event))
			return;
		boolean autostart = PersistencePropertySource.getBoolean(super.getPropertySource(), Directives.AUTO_START );
		if(!autostart )
			return;
		this.setPersistedProperty(event);
		super.setCanCreate(true);
		super.createComponent();
		if(!( event.getFactory().getPropertySource() instanceof IPropertyEventDispatcher))
			return;
		IPropertyEventDispatcher dispatcher = (IPropertyEventDispatcher) event.getFactory().getPropertySource();
		PersistenceService<String,Object> service = (PersistenceService<String, Object>) super.getComponent();
		if( service == null )
			stack.push( dispatcher );
		else
			service.addDispatcher(dispatcher);
	}

	/**
	 * Set the persisted property
	 * @param event
	 */
	protected void setPersistedProperty( ComponentBuilderEvent<Object> event ){
		IJp2pPropertySource<IJp2pProperties> source = event.getFactory().getPropertySource();
		Iterator<IJp2pProperties> iterator = source.propertyIterator();
		while( iterator.hasNext()){
			IJp2pProperties id = iterator.next();
			ManagedProperty<IJp2pProperties,Object> mp = source.getManagedProperty( id );
			if(!ManagedProperty.isPersisted( mp ))
				continue;
			IPersistedProperties<String> properties = new PersistedProperties( (IJp2pWritePropertySource<IJp2pProperties>) source );
			IPropertyConvertor<String, Object> convertor = new Jp2pContext().getConvertor((IJp2pWritePropertySource<IJp2pProperties>) source); 
			properties.setProperty( source, id, convertor.convertFrom( id ));	
		}
	}
	
	@Override
	public synchronized IJp2pComponent<IManagedPropertyListener<IJp2pProperties, Object>> createComponent() {
		return super.createComponent();
	}
	
	@Override
	protected IJp2pComponent<IManagedPropertyListener<IJp2pProperties, Object>> onCreateComponent(
			IJp2pPropertySource<IJp2pProperties> source) {
		IPersistedProperties<String> properties = new PersistedProperties( (IJp2pWritePropertySource<IJp2pProperties>) source );
		IJp2pContext<?> context = new Jp2pContext(); 
		PersistenceService<String,Object> service = new PersistenceService<String,Object>( (IJp2pWritePropertySource<IJp2pProperties>) source, properties, context );
		while( stack.size() > 0)
			service.addDispatcher( stack.pop() );
		service.start();
		stack = null;
		return service;
	}
}