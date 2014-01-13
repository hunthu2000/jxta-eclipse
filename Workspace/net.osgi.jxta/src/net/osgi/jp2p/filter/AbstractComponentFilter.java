package net.osgi.jp2p.filter;

import net.osgi.jp2p.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;

public abstract class AbstractComponentFilter<T,U extends Object> extends AbstractBuilderEventFilter<T> {
	
	public static final String S_ERR_INVALID_EVENT = "The event is not valid; should be CreateComponent or StartComponent";
	
	//The component that has to be created or started
	private U component;
	
	public AbstractComponentFilter( BuilderEvents event, IComponentFactory<T> factory) {
		super(event, factory);
		if(!BuilderEvents.COMPONENT_CREATED.equals(event) && 
				!BuilderEvents.COMPONENT_STARTED.equals(event ) && 
				!BuilderEvents.FACTORY_COMPLETED.equals(event ))
			throw new IllegalArgumentException( S_ERR_INVALID_EVENT );
	}

	protected U getComponent() {
		return component;
	}

	/**
	 * Returns true if the component is correct
	 * @param component
	 * @return
	 */
	protected abstract boolean checkComponent( IComponentFactory<U> factory );
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean onCorrectBuilderEvent(ComponentBuilderEvent<?> event) {
		if( event.getFactory().getComponent() == null )
			return false;
		if( this.checkComponent((IComponentFactory<U>) event.getFactory() )){
			component = (U) event.getFactory().getComponent();
			return true;
		}
		return false;
	}
}