package net.osgi.jxse.service.activator;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.context.IJxseServiceContext;
import net.osgi.jxse.service.xml.XMLServiceContext;

public class JxseBundleActivator extends AbstractJxseBundleActivator {

	String plugin_id;
	
	public JxseBundleActivator(String plugin_id) {
		this.plugin_id = plugin_id;
	}

	@Override
	protected IJxseServiceContext<NetworkManager> createContext() {
		return 	new XMLServiceContext( plugin_id, this.getClass() );
	}
}
