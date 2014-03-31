package net.jp2p.container.service;

import java.util.EventObject;

import net.jp2p.container.service.IServiceListener.ServiceRegistrationEvents;

public class ServiceListenerEvent<T extends Object> extends EventObject {
	private static final long serialVersionUID = 5928582157413151939L;
	
	private ServiceRegistrationEvents event;
	private T data;
	
	public ServiceListenerEvent( Object arg0, ServiceRegistrationEvents event, T data ) {
		super(arg0);
		this.event = event;
		this.data = data;
	}

	public ServiceRegistrationEvents getServiceListenerEvent() {
		return event;
	}

	public T getData() {
		return data;
	}	
}
