package net.osgi.jxse.service;

import java.util.EventObject;

import net.osgi.jxse.service.IServiceChangedListener.ServiceChange;

public class ServiceChangedEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	private ServiceChange change;
	
	public ServiceChangedEvent(Object arg0, ServiceChange change ) {
		super(arg0);
		this.change = change;
	}

	protected ServiceChange getChange() {
		return change;
	}
}
