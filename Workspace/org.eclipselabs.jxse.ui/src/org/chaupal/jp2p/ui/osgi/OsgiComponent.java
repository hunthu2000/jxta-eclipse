package org.chaupal.jp2p.ui.osgi;

import org.eclipselabs.osgi.ds.broker.service.AbstractAttendeeProviderComponent;

public class OsgiComponent extends AbstractAttendeeProviderComponent {

	@Override
	protected void initialise() {
		super.addAttendee( PropertySourcePetitioner.getInstance() );
	}

	@Override
	protected void finalise() {
		super.finalise();
	}
}
