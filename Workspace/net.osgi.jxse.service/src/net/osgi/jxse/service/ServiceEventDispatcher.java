package net.osgi.jxse.service;

import java.util.ArrayList;
import java.util.Collection;

public class ServiceEventDispatcher {

	private Collection<IServiceChangedListener> listeners;

	private static ServiceEventDispatcher dispatcher = new ServiceEventDispatcher();
	
	private ServiceEventDispatcher() {
		this.listeners = new ArrayList<IServiceChangedListener>();
	}
	
	public static ServiceEventDispatcher getInstance(){
		return dispatcher;
	}

	public void addServiceChangeListener( IServiceChangedListener listener ){
		this.listeners.add( listener );
	}

	public void removeServiceChangeListener( IServiceChangedListener listener ){
		this.listeners.remove( listener );
	}
	
	public void serviceChanged( ServiceChangedEvent event ){
		for( IServiceChangedListener listener: this.listeners )
			listener.notifyServiceChanged(event);
	}

}
