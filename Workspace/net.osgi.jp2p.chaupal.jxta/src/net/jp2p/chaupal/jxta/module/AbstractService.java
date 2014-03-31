package net.jp2p.chaupal.jxta.module;

import java.util.ArrayList;
import java.util.Collection;

import net.jp2p.chaupal.jxta.Activator;
import net.jp2p.container.service.IServiceListener;
import net.jp2p.container.service.ServiceListenerEvent;
import net.jp2p.container.service.IServiceListener.ServiceRegistrationEvents;

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
public abstract class AbstractService<T extends Object> {

	private static final String S_ERR_NO_FACTORY_PROVIDED = "No factory is created. Please implement one";
	
	private BundleContext  bc;
	private Collection<IServiceListener<T>> listeners;
	
	private String filter;
	private Class<T> clss;
	
	@SuppressWarnings("unchecked")
	protected AbstractService(BundleContext bc, Class<?> clss, String filter) {
		this.bc = bc;
		this.clss = (Class<T>) clss;
		this.filter = filter;
		listeners = new ArrayList<IServiceListener<T>>();
	}

	public void addServiceEventListeners( IServiceListener<T> listener ){
		this.listeners.add( listener);
	}
	
	public void removeServiceEventListeners( IServiceListener<T> listener ){
		this.listeners.remove( listener);
	}

	protected void notifyServiceEvent( ServiceListenerEvent<T> event ){
		for( IServiceListener<T> listener: this.listeners )
			listener.notifyModuleServiceChanged(event);
	}
	
	/**
	 * Open the service
	 */
	public void open() {
		ServiceListener sl = new ServiceListener() {
			@Override
			@SuppressWarnings("unchecked")
			public void serviceChanged(ServiceEvent ev) {
				ServiceReference<T> sr = (ServiceReference<T>) ev.getServiceReference();
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
			Collection<ServiceReference<T>> srl = bc.getServiceReferences( clss, filter);
			for(ServiceReference<T> sr: srl ) {
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void register(ServiceReference<T> sr) {
		try {
			T service = bc.getService( sr );
			if( service == null )
				throw new NullPointerException( S_ERR_NO_FACTORY_PROVIDED);
			this.notifyServiceEvent( new ServiceListenerEvent(this, ServiceRegistrationEvents.REGISTERED, service));
		} catch (Exception e) {
			Activator.getLog().log( LogService.LOG_WARNING,"Failed to register resource", e);
		}
	}

	/**
	 * Register the service
	 * @param sr
	 */
	private void unregister(ServiceReference<T> sr) {
		try {
			T service = bc.getService( sr );
			if( service == null )
				throw new NullPointerException( S_ERR_NO_FACTORY_PROVIDED);
		} catch (Exception e) {
			Activator.getLog().log( LogService.LOG_WARNING,"Failed to unregister resource", e);
		}
	}

	public void close(){
		try {
			Collection<ServiceReference<T>> srl = bc.getServiceReferences( clss, filter);
			for(ServiceReference<T> sr: srl ) {
				unregister( sr );
			}
		} catch (InvalidSyntaxException e) { 
			e.printStackTrace(); 
		}		
	}
}
