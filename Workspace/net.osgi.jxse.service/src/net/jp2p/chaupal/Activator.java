package net.jp2p.chaupal;

import net.jp2p.chaupal.context.Jp2pContextService;
import net.jp2p.chaupal.module.IServiceListenerContainer;
import net.jp2p.chaupal.module.ModuleFactoryService;
import net.jp2p.container.context.ContextLoader;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class Activator implements BundleActivator {

	public static final String BUNDLE_ID = "net.jp2p.chaupal";
	
	private static Activator plugin;
	private static Jp2pLogService logService;
	
	private static ModuleFactoryService moduleService; 
	
	private static Jp2pContextService contextService; 
	
	private ContextLoader contextLoader;

	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		
		logService = new Jp2pLogService( context );
		logService.open();

		moduleService = new ModuleFactoryService( context );
		moduleService.open();	
		
		contextLoader = ContextLoader.getInstance();
		contextService = new Jp2pContextService( contextLoader, context );
		contextService.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		ServiceReference<?> reference = context.getServiceReference(IServiceListenerContainer.class);
		context.ungetService(reference); 
		
		moduleService.close();
		moduleService = null;

		contextService.close();
		contextService = null;
		contextLoader.clear();
		contextLoader = null;

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
