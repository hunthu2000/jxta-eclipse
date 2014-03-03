package net.jp2p.chaupal;

import java.util.Collection;

import net.jxse.module.IModuleFactory;
import net.jxse.platform.IJxtaModuleLoader;
import net.jxse.platform.IJxtaModuleFactory;
import net.jxse.platform.JxtaModuleContainer;

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
    private String filter = "(objectclass=" + IJxtaModuleFactory.class.getName() + ")";
	
	BundleContext  bc;
	private IJxtaModuleLoader container = JxtaModuleContainer.getInstance();

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
				ServiceReference<IJxtaModuleFactory> sr = (ServiceReference<IJxtaModuleFactory>) ev.getServiceReference();
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
			Collection<ServiceReference<IJxtaModuleFactory>> srl = bc.getServiceReferences( IJxtaModuleFactory.class, filter);
			for(ServiceReference<IJxtaModuleFactory> sr: srl ) {
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
	private void register(ServiceReference<IJxtaModuleFactory> sr) {
		try {
			IJxtaModuleFactory factory = bc.getService( sr );
			if( factory == null )
				throw new NullPointerException( S_ERR_NO_FACTORY_PROVIDED);
			container.addFactory( factory );
			Activator.getLog().log( LogService.LOG_INFO,"Module Factory " + factory.getComponentName() + " registered." );
		} catch (Exception e) {
			Activator.getLog().log( LogService.LOG_WARNING,"Failed to register resource", e);
		}
	}

	/**
	 * Register the service
	 * @param sr
	 */
	private void unregister(ServiceReference<IJxtaModuleFactory> sr) {
		try {
			IJxtaModuleFactory factory = bc.getService( sr );
			if( factory == null )
				throw new NullPointerException( S_ERR_NO_FACTORY_PROVIDED);
			container.removeFactory( factory );
			Activator.getLog().log( LogService.LOG_INFO,"Module Factory " + factory.getComponentName() + " unregistered." );
		} catch (Exception e) {
			Activator.getLog().log( LogService.LOG_WARNING,"Failed to unregister resource", e);
		}
	}

	public void close(){
		String filter = "(objectclass=" + IModuleFactory.class.getName() + ")";

		try {
			Collection<ServiceReference<IJxtaModuleFactory>> srl = bc.getServiceReferences( IJxtaModuleFactory.class, filter);
			for(ServiceReference<IJxtaModuleFactory> sr: srl ) {
				unregister( sr );
				container.removeFactory( bc.getService( sr ));
			}
		} catch (InvalidSyntaxException e) { 
			e.printStackTrace(); 
		}		
	}
}
