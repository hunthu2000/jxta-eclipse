package net.osgi.jp2p.jxta.osgi;

import org.eclipselabs.osgi.ds.broker.service.AbstractAttendeeProviderComponent;

public class OsgiComponent extends AbstractAttendeeProviderComponent {

	@Override
	protected void initialise() {
		super.addAttendee( Jp2pFactoryProvider.getInstance() );
	}
}
