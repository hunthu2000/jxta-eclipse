package net.osgi.jp2p.chaupal.osgi;

import org.eclipselabs.osgi.ds.broker.service.AbstractAttendeeProviderComponent;

public class OsgiComponent extends AbstractAttendeeProviderComponent {

	@Override
	protected void initialise() {
		super.addAttendee( Jp2pFactoryPetitioner.getInstance() );
	}
}
