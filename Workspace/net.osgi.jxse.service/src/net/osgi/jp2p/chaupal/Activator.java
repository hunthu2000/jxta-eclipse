package net.osgi.jp2p.chaupal;

import net.osgi.jp2p.chaupal.module.IServiceListenerContainer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class Activator implements BundleActivator {

	public static final String BUNDLE_ID = "org.chaupal.jp2p.ui";
	
	private static Activator plugin;
	private static Jp2pLogService logService;
	
	private static ModuleFactoryService moduleService; 
	

	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		moduleService = new ModuleFactoryService( context );
		moduleService.open();
		
		logService = new Jp2pLogService( context );
		logService.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		ServiceReference<?> reference = context.getServiceReference(IServiceListenerContainer.class);
		context.ungetService(reference); 
		
		moduleService.close();
		moduleService = null;
		
		logService.close();
		logService = null;
		plugin = null;
	}	
	
	protected ModuleFactoryService getModuleService() {
		return moduleService;
	}

	public static LogService getLog(){
		return logService.getLogService();
	}
	
	public static Activator getDefault(){
		return plugin;
	}
}
