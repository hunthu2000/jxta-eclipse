package net.jp2p.chaupal.module;

import java.util.Collection;

import net.jp2p.chaupal.Activator;
import net.jxse.module.IModuleService;
import net.jxta.impl.loader.DynamicJxtaLoader;

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
    private String filter = "(objectclass=" + IModuleService.class.getName() + ")";
	
	private BundleContext  bc;
	private DynamicJxtaLoader loader = (DynamicJxtaLoader) DynamicJxtaLoader.getInstance();
	
	public ModuleFactoryService(BundleContext bc) {
		this.bc = bc;
	}

	/**
	 * Open the service
	 */
	public void open() {
		ServiceListener sl = new ServiceListener() {
			@SuppressWarnings("unchecked")
			public void serviceChanged(ServiceEvent ev) {
				ServiceReference<IModuleService> sr = (ServiceReference<IModuleService>) ev.getServiceReference();
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
			Collection<ServiceReference<IModuleService>> srl = bc.getServiceReferences( IModuleService.class, filter);
			for(ServiceReference<IModuleService> sr: srl ) {
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
	private void register(ServiceReference<IModuleService> sr) {
		try {
			IModuleService module = bc.getService( sr );
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
	private void unregister(ServiceReference<IModuleService> sr) {
		try {
			IModuleService module = bc.getService( sr );
			if( module == null )
				throw new NullPointerException( S_ERR_NO_FACTORY_PROVIDED);
			Activator.getLog().log( LogService.LOG_INFO,"Module Factory " + module.getIdentifier() + " unregistered." );
		} catch (Exception e) {
			Activator.getLog().log( LogService.LOG_WARNING,"Failed to unregister resource", e);
		}
	}

	public void close(){
		String filter = "(objectclass=" + IModuleService.class.getName() + ")";

		try {
			Collection<ServiceReference<IModuleService>> srl = bc.getServiceReferences( IModuleService.class, filter);
			for(ServiceReference<IModuleService> sr: srl ) {
				unregister( sr );
			}
		} catch (InvalidSyntaxException e) { 
			e.printStackTrace(); 
		}		
	}
}
