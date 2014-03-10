package net.jp2p.chaupal.context;

import java.util.ArrayList;
import java.util.Collection;

import net.jp2p.chaupal.Activator;
import net.jp2p.container.context.IJp2pContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.InvalidSyntaxException;
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
public class ContextService {

	private static final String S_ERR_NO_CONTEXT_PROVIDED = "No context is created. Please implement one";
    private String filter = "(objectclass=" + IJp2pContext.class.getName() + ")";
	
	BundleContext  bc;
	private Collection<IJp2pContext> container;

	public ContextService(BundleContext bc) {
		this.bc = bc;
		container = new ArrayList<IJp2pContext>();
	}

	/**
	 * Open the service
	 */
	protected IJp2pContext open( String contextName ) {
		try {
			ServiceReference<?>[] srl = bc.getServiceReferences( contextName, filter);
			for(ServiceReference<?> sr: srl ) {
				return register( sr );
			}
			return null;
		} catch (InvalidSyntaxException e) { 
			e.printStackTrace(); 
			return null;
		}
	}

	public IJp2pContext getContext( String contextName ){
		for( IJp2pContext context: container ){
			if( context.getClass().getCanonicalName().equals( contextName ))
				return context;
		}
		return open( contextName );
	}
	/**
	 * Register the service
	 * @param sr
	 */
	private IJp2pContext register(ServiceReference<?> sr) {
		try {
			IJp2pContext context = (IJp2pContext) bc.getService( sr );
			if( context == null )
				throw new NullPointerException( S_ERR_NO_CONTEXT_PROVIDED);
			container.add( context );
			Activator.getLog().log( LogService.LOG_INFO,"Context " + context.getName() + " registered." );
			return context;
		} catch (Exception e) {
			Activator.getLog().log( LogService.LOG_WARNING,"Failed to register resource", e);
			return null;
		}
	}

	/**
	 * Register the service
	 * @param sr
	 */
	private void unregister(ServiceReference<?> sr) {
		try {
			IJp2pContext context = (IJp2pContext) bc.getService( sr );
			if( context == null )
				throw new NullPointerException( S_ERR_NO_CONTEXT_PROVIDED);
			container.remove( context );
			Activator.getLog().log( LogService.LOG_INFO,"Module Factory " + context.getName() + " unregistered." );
		} catch (Exception e) {
			Activator.getLog().log( LogService.LOG_WARNING,"Failed to unregister resource", e);
		}
	}

	protected void close( IJp2pContext context ){
		String filter = "(objectclass=" + context.getName() + ")";

		try {
			ServiceReference<?>[] srl = bc.getServiceReferences( context.getName(), filter);
			for(ServiceReference<?> sr: srl ) {
				unregister( sr );
				container.remove( bc.getService( sr ));
			}
		} catch (InvalidSyntaxException e) { 
			e.printStackTrace(); 
		}		
	}

	public void close(){
		for(IJp2pContext context: container ) {
			close( context );
		}
	}

}
