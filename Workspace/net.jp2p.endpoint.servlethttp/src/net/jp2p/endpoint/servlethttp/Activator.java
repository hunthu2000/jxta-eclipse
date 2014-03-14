package net.jp2p.endpoint.servlethttp;

import net.jp2p.endpoint.servlethttp.osgi.ModuleFactoryRegistrator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static ModuleFactoryRegistrator mfr;
	
	@Override
	public void start(BundleContext context) throws Exception {
		mfr = new ModuleFactoryRegistrator(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		mfr.unregister();
		mfr = null;		
	}

	public static ModuleFactoryRegistrator getModuleFactoryRegistrator(){
		return mfr;
	}
}