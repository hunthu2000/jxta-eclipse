package net.osgi.jp2p.chaupal;

import net.jxse.osgi.module.IJp2pServiceListener;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class Activator implements BundleActivator {

	public static final String BUNDLE_ID = "org.chaupal.jp2p.ui";
	
	private static Activator plugin;
	private static Jp2pLogService logService;
	
	private static IJp2pServiceListener service;
	
	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		
		ServiceReference<?> serviceReference = context.getServiceReference(IJp2pServiceListener.class.getName());
		service = (IJp2pServiceListener) context.getService(serviceReference); 
		logService = new Jp2pLogService( context );
		logService.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		logService.close();
		logService = null;
		plugin = null;
	}	
	
	public static IJp2pServiceListener getService() {
		return service;
	}

	public static LogService getLog(){
		return logService.getLogService();
	}
	
	public static Activator getDefault(){
		return plugin;
	}
}
