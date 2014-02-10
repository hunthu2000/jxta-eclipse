package net.osgi.jp2p.chaupal;

import net.jxse.module.IJp2pServiceListener;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	public static final String BUNDLE_ID = "org.chaupal.jp2p.ui";
	
	private static Activator plugin;
	private ServiceTracker<BundleContext,LogService> logServiceTracker;
	private static LogService logService;
	
	private static IJp2pServiceListener service;
	
	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		
		ServiceReference<?> serviceReference = context.getServiceReference(IJp2pServiceListener.class.getName());
		service = (IJp2pServiceListener) context.getService(serviceReference); 
		
		// create a tracker and track the log service
		logServiceTracker = 
			new ServiceTracker<BundleContext,LogService>(context, LogService.class.getName(), null);
		logServiceTracker.open();
		
		// grab the service
		logService = logServiceTracker.getService();

		if(logService != null)
			logService.log(LogService.LOG_INFO, "Logging service started");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if(logService != null)
			logService.log(LogService.LOG_INFO, "Logging service Stopped");
		
		// close the service tracker
		logServiceTracker.close();
		logServiceTracker = null;
		plugin = null;
	}	
	
	public static IJp2pServiceListener getService() {
		return service;
	}

	public static LogService getLog(){
		return logService;
	}
	
	public static Activator getDefault(){
		return plugin;
	}
}
