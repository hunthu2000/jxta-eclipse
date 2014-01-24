package org.chaupal.jp2p.ui.osgi.service;

import org.eclipselabs.osgi.ds.broker.service.AbstractAttendeeProviderComponent;

public class OsgiComponent extends AbstractAttendeeProviderComponent {

	@Override
	protected void initialise() {
		super.addAttendee( Jp2pServiceContainerPetitioner.getInstance() );
		super.addAttendee( MessageBoxProvider.getInstance() );
	}

	@Override
	protected void finalise() {
		 Jp2pServiceContainerPetitioner.getInstance().finalise();
		MessageBoxProvider.getInstance().finalise();	
		super.finalise();
	}
}
