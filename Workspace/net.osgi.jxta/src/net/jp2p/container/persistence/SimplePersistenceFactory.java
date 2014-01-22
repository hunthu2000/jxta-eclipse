package net.jp2p.container.persistence;

import java.util.Stack;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.factory.AbstractComponentFactory;
import net.jp2p.container.factory.ComponentBuilderEvent;
import net.jp2p.container.filter.BuilderEventFilter;
import net.jp2p.container.filter.IComponentFactoryFilter;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IManagedPropertyListener;
import net.jp2p.container.properties.IPropertyConvertor;
import net.jp2p.container.properties.IPropertyEventDispatcher;

public class SimplePersistenceFactory extends AbstractComponentFactory<IManagedPropertyListener<IJp2pProperties, Object>> {

	PersistenceService<String,Object> service;
	private Stack<IPropertyEventDispatcher> stack;
	
	private IComponentFactoryFilter filter = new BuilderEventFilter<IJp2pComponent<IManagedPropertyListener<IJp2pProperties, Object>>>( BuilderEvents.PROPERTY_SOURCE_CREATED, this );

	
	public SimplePersistenceFactory(IContainerBuilder builder,
			IJp2pPropertySource<IJp2pProperties> parentSource ) {
		super(builder, parentSource);
		stack = new Stack<IPropertyEventDispatcher>();
	}

	@Override
	protected IJp2pPropertySource<IJp2pProperties> onCreatePropertySource() {
		IJp2pPropertySource<IJp2pProperties> source = new PersistencePropertySource( super.getParentSource() );
		super.setCanCreate(true);
		return source;
	}

	
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		if( !filter.accept(event))
			return;
		if(!( event.getFactory().getPropertySource() instanceof IPropertyEventDispatcher))
			return;
		IPropertyEventDispatcher dispatcher = (IPropertyEventDispatcher) event.getFactory().getPropertySource();
		if( service == null )
			stack.push( dispatcher );
		else
			service.addDispatcher(dispatcher);
	}

	
	@Override
	public synchronized IJp2pComponent<IManagedPropertyListener<IJp2pProperties, Object>> createComponent() {
		return super.createComponent();
	}

	
	@Override
	protected IJp2pComponent<IManagedPropertyListener<IJp2pProperties, Object>> onCreateComponent(
			IJp2pPropertySource<IJp2pProperties> source) {
		IPersistedProperties<String> properties = new PersistedProperties( (IJp2pWritePropertySource<IJp2pProperties>) source );
		IPropertyConvertor<String, Object> convertor = Jp2pContext.getConvertor((IJp2pWritePropertySource<IJp2pProperties>) source); 
		service = new PersistenceService<String,Object>( (IJp2pWritePropertySource<IJp2pProperties>) source, properties, convertor );
		while( stack.size() > 0)
			service.addDispatcher( stack.pop() );
		service.start();
		stack = null;
		return service;
	}

}
