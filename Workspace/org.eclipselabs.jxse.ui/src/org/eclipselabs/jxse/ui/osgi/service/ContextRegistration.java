package org.eclipselabs.jxse.ui.osgi.service;

import net.osgi.jxse.component.IJxseComponent;

public class ContextRegistration{

	private JxseServiceContainerPetitioner petitioner = JxseServiceContainerPetitioner.getInstance();
	
	
	public ContextRegistration() {
		super();
	}

	public void register(IJxseComponent<?> service) {
		petitioner.addChild( service );
	}

	public void unregister(IJxseComponent<?> service) {
		petitioner.removeChild( service );
	}
}