package net.jp2p.chaupal.core;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public abstract class AbstractRegistrator<T extends Object> implements BundleActivator {

	private T registered;
	private String identifier;
	
	protected AbstractRegistrator( String identifier ) {
		super();
		this.identifier = identifier;
	}

	protected abstract T createRegisteredObject();

	protected abstract void fillDictionary( Dictionary<String,Object> dictionary );

	public String getIdentifier() {
		return identifier;
	}

	protected T getRegistered() {
		return registered;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		registered = this.createRegisteredObject();
		Dictionary<String,Object> dict = new Hashtable<String, Object>();
		this.fillDictionary( dict );
		context.registerService( identifier, registered, dict );
	}

	@SuppressWarnings("unchecked")
	@Override
	public void stop(BundleContext context) throws Exception {
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
