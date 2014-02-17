package net.osgi.jp2p.chaupal.module;

import net.jxse.osgi.module.IJp2pServiceListener;
import net.osgi.jp2p.chaupal.Activator;

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
public abstract class AbstractModuleListenerService {

	private static final String S_ERR_NO_LISTENER_PROVIDED = "No listener is created. Please implement one";
	BundleContext  bc;
	
	private IJp2pServiceListener listener;

	public AbstractModuleListenerService(BundleContext bc) {
		this.bc = bc;
	}

	/**
	 * Open the service
	 */
	public void open() {
		ServiceListener sl = new ServiceListener() {
			public void serviceChanged(ServiceEvent ev) {
				ServiceReference<?> sr = ev.getServiceReference();
				switch(ev.getType()) {
				case ServiceEvent.REGISTERED:
				{
					try {
						register(sr);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				}
			}
		};

		String filter = "(objectclass=" + IJp2pServiceListener.class.getName() + ")";

		try {
			bc.addServiceListener(sl, filter);
			ServiceReference<?>[] srl = bc.getServiceReferences((String)null, filter);
			for(int i = 0; srl != null && i < srl.length; i++) {
				register(srl[i]);
			}
		} catch (InvalidSyntaxException e) { 
			e.printStackTrace(); 
		}
	}
	
	protected abstract IJp2pServiceListener createServiceListener();
	/**
	 * Register the service
	 * @param sr
	 */
	private void register(ServiceReference<?> sr) {
		IServiceListenerContainer container = (IServiceListenerContainer) bc.getService(sr);
		if (container == null) {
			Activator.getLog().log( LogService.LOG_WARNING, "No JXTA module container");
			return;
		}

		try {
			listener  = this.createServiceListener();
			if( listener == null )
				throw new NullPointerException( S_ERR_NO_LISTENER_PROVIDED);
			container.addListener( listener );
		} catch (Exception e) {
			Activator.getLog().log( LogService.LOG_WARNING,"Failed to register resource", e);
		}
	}

	/**
	 * Register the service
	 * @param sr
	 */
	private void unregister(ServiceReference<?> sr) {
		IServiceListenerContainer container = (IServiceListenerContainer) bc.getService(sr);
		if (container == null) {
			Activator.getLog().log( LogService.LOG_WARNING, "No JXTA module container");
			return;
		}

		try {
			container.removeListener( listener );
		} catch (Exception e) {
			Activator.getLog().log( LogService.LOG_WARNING,"Failed to register resource", e);
		}
	}

	public void close(){
		String filter = "(objectclass=" + IJp2pServiceListener.class.getName() + ")";

		try {
			ServiceReference<?>[] srl = bc.getServiceReferences((String)null, filter);
			for(int i = 0; srl != null && i < srl.length; i++) {
				unregister(srl[i]);
			}
		} catch (InvalidSyntaxException e) { 
			e.printStackTrace(); 
		}		
	}
}
