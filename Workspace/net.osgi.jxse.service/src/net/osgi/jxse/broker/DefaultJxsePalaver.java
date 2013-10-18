package net.osgi.jxse.broker;

import org.eclipselabs.osgi.ds.broker.service.AbstractPalaver;

/**
 * The palaver contains the conditions for attendees to create an assembly. In this case, the attendees must
 * pass a string identifier (the package id) and provide a token that is equal
 * @author Kees
 *
 */
public class DefaultJxsePalaver extends AbstractPalaver<String>{

	private static final String S_IJXTACONTAINER_PACKAGE_ID = "org.osgi.jxta.service.ijxtaservicecomponent";
	private static final String S_IJXTA_TOKEN = "org.osgi.jxse.server.token";
	
	protected DefaultJxsePalaver() {
		super(S_IJXTACONTAINER_PACKAGE_ID);
	}

	@Override
	public String giveToken() {
		return S_IJXTA_TOKEN;
	}

	@Override
	public boolean confirm(Object token) {
		return ( token.equals(S_IJXTA_TOKEN ));
	}	
}
