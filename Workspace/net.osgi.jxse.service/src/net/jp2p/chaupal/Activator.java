package net.jp2p.chaupal;

import net.jp2p.chaupal.context.ContextService;
import net.jp2p.chaupal.module.IServiceListenerContainer;
import net.jp2p.chaupal.module.ModuleFactoryService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class Activator implements BundleActivator {

	public static final String BUNDLE_ID = "net.jp2p.chaupal";
	
	private static Activator plugin;
	private static Jp2pLogService logService;
	
	private static ModuleFactoryService moduleService; 
	private static ContextService contextService; 
	

	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		
		logService = new Jp2pLogService( context );
		logService.open();

		moduleService = new ModuleFactoryService( context );
		moduleService.open();
	
		contextService = new ContextService( context );
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		ServiceReference<?> reference = context.getServiceReference(IServiceListenerContainer.class);
		context.ungetService(reference); 
		
		moduleService.close();
		moduleService = null;

		contextService.close();
		contextService = null;

		logService.close();
		logService = null;
		plugin = null;
	}	
	
	protected ModuleFactoryService getModuleService() {
		return moduleService;
	}

	public static ContextService getContextService() {
		return contextService;
	}

	public static LogService getLog(){
		return logService.getLogService();
	}
	
	public static Activator getDefault(){
		return plugin;
	}
}
