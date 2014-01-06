package net.osgi.jp2p.jxta.osgi;

import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.jxta.factory.JxtaFactoryUtils;

import org.eclipselabs.osgi.ds.broker.service.AbstractPalaver;
import org.eclipselabs.osgi.ds.broker.service.AbstractProvider;

public class Jp2pFactoryProvider extends AbstractProvider<String, String, IComponentFactory<?>> {

	public static final String S_JP2P_CONTAINER_PACKAGE_ID = "org.osgi.jp2p.chaupal.core";
	public static final String S_JP2P_TOKEN = "org.osgi.jp2p.token";

	private static Jp2pFactoryProvider attendee = new Jp2pFactoryProvider();
	
	private Jp2pFactoryProvider() {
		super( new MessageBoxPalaver());
	}
	
	public static Jp2pFactoryProvider getInstance(){
		return attendee;
	}

	@Override
	protected void onDataReceived( String componentName ) {
		super.provide( JxtaFactoryUtils.getDefaultFactory(null, null, componentName));
	}
}

class MessageBoxPalaver extends AbstractPalaver<String>{

	protected MessageBoxPalaver() {
		super( Jp2pFactoryProvider.S_JP2P_CONTAINER_PACKAGE_ID );
	}

	@Override
	public String giveToken() {
		return Jp2pFactoryProvider.S_JP2P_TOKEN;
	}

	@Override
	public boolean confirm(Object token) {
		return ( token instanceof String );
	}	
}