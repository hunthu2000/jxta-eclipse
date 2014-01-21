package net.osgi.jp2p.persistence;

import java.util.ArrayList;
import java.util.Collection;

import net.osgi.jp2p.component.AbstractJp2pService;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.IManagedPropertyListener;
import net.osgi.jp2p.properties.IPropertyEventDispatcher;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.properties.ManagedPropertyEvent;

public class PersistenceService extends AbstractJp2pService<IManagedPropertyListener<IJp2pProperties, Object>> {

	private IPersistedProperty<?> property;
	
	private IManagedPropertyListener<IJp2pProperties, Object> listener;
	
	private Collection<IPropertyEventDispatcher> dispatchers;
	
	public PersistenceService(IJp2pWritePropertySource<IJp2pProperties> source, IPersistedProperty<?> props ){
		super(source, null );
		this.property = props;
		dispatchers = new ArrayList<IPropertyEventDispatcher>();
		listener = new IManagedPropertyListener<IJp2pProperties, Object>(){

			@Override
			public void notifyValueChanged(
					ManagedPropertyEvent<IJp2pProperties, Object> event) {
				ManagedProperty<IJp2pProperties, Object> mp = event.getProperty();
				if( !ManagedProperty.isPersisted( mp))
					return;
				switch( event.getEvent() ){
				case DEFAULT_VALUE_SET:
					mp.setValue( property.getProperty( mp.getKey()));
					break;
				default:
					property.setProperty(mp.getKey());
				}
			}
		};
		super.setModule(listener);
	}

	public void addDispatcher( IPropertyEventDispatcher dispatcher ){
		this.dispatchers.add( dispatcher );
		dispatcher.addPropertyListener(listener);
	}
	
	public void removeDispatcher( IPropertyEventDispatcher dispatcher ){
		dispatcher.removePropertyListener(listener);
		this.dispatchers.remove( dispatcher );
	}

	
	@Override
	protected void deactivate() {
		for( IPropertyEventDispatcher dispatcher: this.dispatchers ){
			dispatcher.removePropertyListener(listener);
		}
	}
}
