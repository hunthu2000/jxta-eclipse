package net.jp2p.chaupal.activator;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public abstract class AbstractRegistrator<T extends Object>{

	private T registered;
	private BundleContext context;
	
	private String identifier;
	
	protected AbstractRegistrator( String identifier, BundleContext context ) {
		super();
		this.context = context;
		this.identifier = identifier;
	}

	protected abstract void fillDictionary( Dictionary<String,Object> dictionary );

	public String getIdentifier() {
		return identifier;
	}

	protected T getRegistered() {
		return registered;
	}

	/**
	 * Register the object
	 */
	public void register( T registered ){
		this.registered = registered;
		Dictionary<String,Object> dict = new Hashtable<String, Object>();
		this.fillDictionary( dict );
		context.registerService( identifier, registered, dict );
	}

	@SuppressWarnings("unchecked")
	public void unregister() throws InvalidSyntaxException {
		String filter = "(" + identifier + "=" + registered.getClass().getName() + ")";
		Collection<?> references = context.getServiceReferences( registered.getClass(), filter );
		for( Object obj: references ){
			ServiceReference<T> reference = (ServiceReference<T>) obj;
			if( registered.equals( context.getService(reference))){
				context.ungetService(reference);
				registered = null;
			}
		}
	}
}
