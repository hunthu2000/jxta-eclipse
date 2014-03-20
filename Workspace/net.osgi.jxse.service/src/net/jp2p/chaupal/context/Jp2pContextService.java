package net.jp2p.chaupal.context;

import net.jp2p.container.component.IModuleService;
import net.jp2p.container.context.ContextLoader;
import net.jp2p.container.context.IJp2pContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.InvalidSyntaxException;

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
public class Jp2pContextService {

   private String filterBase = "(jp2p.context="; //in component.xml file you will use target="(jp2p.context=http)"
	
	private BundleContext  bc;
	private ContextLoader loader;
	
	public Jp2pContextService(ContextLoader loader, BundleContext bc) {
		this.bc = bc;
		this.loader = loader;
	}

	/**
	 * Open the service
	 */
	public void open( ) {
		String filter = filterBase + "*" + ")";
		ServiceListener sl = new ServiceListener() {
			@SuppressWarnings("unchecked")
			public void serviceChanged(ServiceEvent ev) {
				ServiceReference<IModuleService<?>> sr = (ServiceReference<IModuleService<?>>) ev.getServiceReference();
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
			ServiceReference<?>[] srl = bc.getServiceReferences( IJp2pContext.class.getName(), filter);
			if(( srl == null ) ||( srl.length == 0 ))
				return;
			for( ServiceReference<?> sr: srl )
				register( sr );
		} catch (InvalidSyntaxException e) { 
			e.printStackTrace(); 
		}
	}

	public void close(){
		
	}
	
	/**
	 * Register the service with the given context name
	 * @param sr
	 */
	private void register(ServiceReference<?> sr ) {
		IJp2pContext context = (IJp2pContext) bc.getService( sr );
		loader.addContext(context);
	}

	/**
	 * Register the service
	 * @param sr
	 */
	private void unregister(ServiceReference<?> sr) {
		IJp2pContext context = (IJp2pContext) bc.getService( sr );
		loader.removeContext(context);
	}
}
