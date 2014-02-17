package net.osgi.jp2p.chaupal.module;

import java.util.ArrayList;
import java.util.Collection;

import net.jxse.osgi.module.IJp2pServiceListener;

public class ServiceListenerContainer implements IServiceListenerContainer {

	private Collection<IJp2pServiceListener> listeners;
	
	private static ServiceListenerContainer container = new ServiceListenerContainer();
	
	private ServiceListenerContainer() {
		listeners = new ArrayList<IJp2pServiceListener>();
	}

	public static ServiceListenerContainer getInstance(){
		return container;
	}
	
	public void clear(){
		this.listeners.clear();
	}

	@Override
	public boolean addListener( IJp2pServiceListener module ) {
		return listeners.add( module );
	}

	@Override
	public void removeListener( IJp2pServiceListener listener ) {
		this.listeners.remove(listener );
	}

	@Override
	public IJp2pServiceListener getListener( String componentName ) {
		return null;
	}
}
