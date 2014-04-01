package net.jp2p.container.service;

public interface IServiceListener<T extends Object> {

	public enum ServiceRegistrationEvents{
		REGISTERED,
		UNREGISTERED;
	}
	
	/**
	 * respond to a module service changed event
	 * @param event
	 */
	public void notifyModuleServiceChanged( ServiceListenerEvent<T> event );
}
