package org.eclipselabs.jxse.ui.osgi.service;

import org.eclipselabs.osgi.ds.broker.service.AbstractAttendeeProviderComponent;

public class OsgiComponent extends AbstractAttendeeProviderComponent {

	@Override
	protected void initialise() {
		super.addAttendee( JxseServiceContainerPetitioner.getInstance() );
		super.addAttendee( MessageBoxProvider.getInstance() );
	}

	@Override
	protected void finalise() {
		MessageBoxProvider.getInstance().finalise();
		super.finalise();
	}
}
