package net.jp2p.endpoint.servlethttp;


import net.jp2p.endpoint.servlethttp.context.ContextRegistrator;
import net.jp2p.endpoint.servlethttp.osgi.ModuleFactoryRegistrator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private ModuleFactoryRegistrator mfr;
	private ContextRegistrator cfr;

	@Override
	public void start(BundleContext context) throws Exception {
		mfr = new ModuleFactoryRegistrator();
		mfr.start(context);

		cfr = new ContextRegistrator();
		cfr.start(context);

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		mfr.stop(context);
		mfr = null;

		cfr.stop(context);
		cfr = null;

	}

}
