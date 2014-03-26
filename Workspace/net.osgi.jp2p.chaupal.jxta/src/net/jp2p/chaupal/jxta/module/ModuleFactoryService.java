package net.jp2p.chaupal.jxta.module;

import java.util.Collection;

import net.jp2p.chaupal.jxta.Activator;
import net.jp2p.jxta.module.IJxtaModuleService;
import net.jxta.impl.loader.DynamicJxtaLoader;
import net.jxta.platform.IJxtaLoader;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.service.log.LogService;

/**
 * <p>
 * Wrapper class which listens for all framework services of
 * class ImoduleFactory. Each such service is picked
 * up and installed into the module container
 * </p>
 * <p>
 * <p>
 * The alias used for the servlet/resource is taken from the 
 * PROP_ALIAS service property.
 * </p>
 * <p>
 * The resource dir used for contexts is taken from the 
 * PROP_DIR service property.
 * </p>
 */
public class ModuleFactoryService {

	private static final String S_ERR_NO_FACTORY_PROVIDED = "No factory is created. Please implement one";
    private String filter = "(objectclass=" + IJxtaModuleService.class.getName() + ")";
	
	private BundleContext  bc;
	private IJxtaLoader loader = DynamicJxtaLoader.getInstance();
	
	public ModuleFactoryService(BundleContext bc) {
		this.bc = bc;
	}

	/**
	 * Open the service
	 */
	@SuppressWarnings("rawtypes")
	public void open() {
		ServiceListener sl = new ServiceListener() {
			@Override
			@SuppressWarnings("unchecked")
			public void serviceChanged(ServiceEvent ev) {
				ServiceReference<IJxtaModuleService> sr = (ServiceReference<IJxtaModuleService>) ev.getServiceReference();
				switch(ev.getType()) {
				case ServiceEvent.REGISTERED:
					try {
						register(sr);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case ServiceEvent.UNREGISTERING:
					try {
						unregister(sr);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
			}
		};

		try {
			bc.addServiceListener(sl, filter);
			Collection<ServiceReference<IJxtaModuleService>> srl = bc.getServiceReferences( IJxtaModuleService.class, filter);
			for(ServiceReference<IJxtaModuleService> sr: srl ) {
				register( sr );
			}
		} catch (InvalidSyntaxException e) { 
			e.printStackTrace(); 
		}
	}
	
	/**
	 * Register the service
	 * @param sr
	 */
	@SuppressWarnings("rawtypes")
	private void register(ServiceReference<IJxtaModuleService> sr) {
		try {
			IJxtaModuleService module = bc.getService( sr );
			if( module == null )
				throw new NullPointerException( S_ERR_NO_FACTORY_PROVIDED);
			loader.defineClass( module.getModuleImplAdvertisement() );
			Activator.getLog().log( LogService.LOG_INFO,"Module Factory " + module.getIdentifier() + " registered." );
		} catch (Exception e) {
			Activator.getLog().log( LogService.LOG_WARNING,"Failed to register resource", e);
		}
	}

	/**
	 * Register the service
	 * @param sr
	 */
	@SuppressWarnings("rawtypes")
	private void unregister(ServiceReference<IJxtaModuleService> sr) {
		try {
			IJxtaModuleService module = bc.getService( sr );
			if( module == null )
				throw new NullPointerException( S_ERR_NO_FACTORY_PROVIDED);
			Activator.getLog().log( LogService.LOG_INFO,"Module Factory " + module.getIdentifier() + " unregistered." );
		} catch (Exception e) {
			Activator.getLog().log( LogService.LOG_WARNING,"Failed to unregister resource", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public void close(){
		String filter = "(objectclass=" + IJxtaModuleService.class.getName() + ")";

		try {
			Collection<ServiceReference<IJxtaModuleService>> srl = bc.getServiceReferences( IJxtaModuleService.class, filter);
			for(ServiceReference<IJxtaModuleService> sr: srl ) {
				unregister( sr );
			}
		} catch (InvalidSyntaxException e) { 
			e.printStackTrace(); 
		}		
	}
}
