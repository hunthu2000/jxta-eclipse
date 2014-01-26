package org.chaupal.jp2p.ui.jxta.osgi.service;

import org.eclipselabs.osgi.ds.broker.service.AbstractAttendeeProviderComponent;

public class OsgiComponent extends AbstractAttendeeProviderComponent {

	@Override
	protected void initialise() {
		super.addAttendee( PropertySourceProvider.getInstance() );
		super.addAttendee( PeerGroupPetitioner.getInstance() );
	}

	@Override
	protected void finalise() {
		PeerGroupPetitioner.getInstance().finalise();
		super.finalise();
	}
}
