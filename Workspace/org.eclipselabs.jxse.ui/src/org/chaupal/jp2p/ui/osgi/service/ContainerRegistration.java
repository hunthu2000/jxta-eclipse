package org.chaupal.jp2p.ui.osgi.service;

import net.osgi.jp2p.component.IJp2pComponent;

public class ContainerRegistration{

	private Jp2pServiceContainerPetitioner petitioner = Jp2pServiceContainerPetitioner.getInstance();
	
	
	public ContainerRegistration() {
		super();
	}

	public void register(IJp2pComponent<?> service) {
		petitioner.addChild( service );
	}

	public void unregister(IJp2pComponent<?> service) {
		petitioner.removeChild( service );
	}
}