package net.jp2p.endpoint.servlethttp;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext bcontext;
	

	@Override
	public void start(BundleContext context) throws Exception {
		bcontext = context; 
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		bcontext = null;
	}

	public static BundleContext getBundleContext(){
		return bcontext;
	}
}
