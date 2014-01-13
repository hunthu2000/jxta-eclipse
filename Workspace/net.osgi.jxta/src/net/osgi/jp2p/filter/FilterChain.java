package net.osgi.jp2p.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;

public class FilterChain<T extends Object> extends AbstractComponentFactoryFilter<T> {

	//Supported operators
	public enum Operators{
		AND,
		SEQUENTIAL_AND,
		OR;
	}
	
	private Operators operator;
	
	private Map<IComponentFactoryFilter, Boolean> filters;
	
	private Collection<IFilterChainListener> listeners;
	
	private boolean completed;
	
	public FilterChain( Operators operator, IComponentFactory<T> factory) {
		super(factory);
		this.operator = operator;
		filters = new HashMap<IComponentFactoryFilter, Boolean>();
		listeners = new ArrayList<IFilterChainListener>();
		this.completed = false;
	}

	public FilterChain( IComponentFactory<T> factory) {
		this( Operators.AND, factory);
	}

	public boolean addFilter( IComponentFactoryFilter filter ){
		this.filters.put( filter, Boolean.FALSE );
		return true;
	}

	public boolean removeFilter( IComponentFactoryFilter filter ){
		return this.filters.remove( filter );
	}

	public boolean addListener( IFilterChainListener listener ){
		return this.listeners.add( listener );
	}

	public boolean removeListener( IFilterChainListener listener ){
		return this.listeners.remove( listener );
	}

	protected void notifyFilterAccept( IComponentFactoryFilter filter, IComponentFactory<?> factory){
		if( !filter.hasAccepted() )
			return;
		for( IFilterChainListener listener: this.listeners )
			listener.notifyComponentCompleted( new FilterChainEvent( filter, factory ));
	}
	
	@Override
	public boolean onAccept(ComponentBuilderEvent<?> event) {
		if( completed )
			return false;
		boolean accept = false;	
		switch( operator ){
			case OR:
				for( IComponentFactoryFilter filter: this.filters.keySet() ){
					accept = filter.accept(event);
					filters.put(filter, accept);
					if( accept ){
						completed = true;
						this.notifyFilterAccept(filter, event.getFactory());
						return true;
					}
				}			
			case SEQUENTIAL_AND:
				boolean retval = true;
				for( IComponentFactoryFilter filter: this.filters.keySet() ){
					accept = filter.hasAccepted() || filter.accept(event);
					retval &= accept;
					filters.put(filter, accept);
					this.notifyFilterAccept(filter, event.getFactory());
				}
				if( retval )
					completed = true;
				return retval;
			default:
				for( IComponentFactoryFilter filter: this.filters.keySet() ){
					accept = filter.accept(event);
					filters.put(filter, accept);
					if( !accept )
						return false;
					else{
						this.notifyFilterAccept(filter, event.getFactory());
						completed = true;
					}
				}
			}
		return true;
	}
}
