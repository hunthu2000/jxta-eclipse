package net.jp2p.chaupal.jxta.network;

import net.jp2p.chaupal.jxta.module.ModuleFactoryRegistrator;
import net.jp2p.chaupal.jxta.network.module.JxtaContextService;
import net.jp2p.container.context.ContextLoader;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public class Activator implements BundleActivator {

	public static final String BUNDLE_ID = "net.jp2p.chaupal.jxta.network";
	
	private static Activator plugin;
	
	private static Jp2pLogService logService;
	private static ModuleFactoryRegistrator mfr;
	private static JxtaContextService contextService; 
	
	private static ContextLoader loader;
	
	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		
		loader = ContextLoader.getInstance();
		contextService = new JxtaContextService( context );
		contextService.open();			

		mfr = new ModuleFactoryRegistrator(context);

		logService = new Jp2pLogService( context );
		logService.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {		

		mfr.unregister();
		mfr = null;

		contextService.close();
		contextService = null;
		loader = null;

		logService.close();
		logService = null;
		plugin = null;
	}	
	
	public static LogService getLog(){
		return logService.getLogService();
	}
	
	public static Activator getDefault(){
		return plugin;
	}

	public static ModuleFactoryRegistrator getModuleFactoryRegistrator(){
		return mfr;
	}

	public static ContextLoader getContextLoader() {
		return loader;
	}
}
