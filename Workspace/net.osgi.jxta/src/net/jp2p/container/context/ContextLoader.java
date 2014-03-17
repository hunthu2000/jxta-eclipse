package net.jp2p.container.context;

import java.util.ArrayList;
import java.util.Collection;

import net.jp2p.container.context.ContextLoaderEvent.LoaderEvent;
import net.jp2p.container.utils.Utils;

public class ContextLoader {

	private static final int DEFAULT_TIME = 200;
	private static final int DEFAULT_COUNT = 50;

	private Collection<IJp2pContext> contexts;
	
	private static ContextLoader contextLoader = new ContextLoader();

	private Collection<IContextLoaderListener> listeners;
	
	private ContextLoader() {
		contexts = new ArrayList<IJp2pContext>();
		this.listeners = new ArrayList<IContextLoaderListener>();
	}

	public static ContextLoader getInstance(){
		return contextLoader;
	}

	public void clear(){
		contexts.clear();
	}

	public void addContextLoaderListener( IContextLoaderListener listener ){
		this.listeners.add( listener );
	}

	public void removeContextLoaderListener( IContextLoaderListener listener ){
		this.listeners.remove( listener );
	}

	private final void notifyContextChanged( ContextLoaderEvent event ){
		for( IContextLoaderListener listener: listeners)
			listener.notifyRegisterContext(event);
	}

	public void addContext( IJp2pContext context ){
		this.contexts.add( context );
		notifyContextChanged( new ContextLoaderEvent( this, LoaderEvent.REGISTERED, context ));		
	}

	public void removeContext( IJp2pContext context ){
		this.contexts.remove( context );
		notifyContextChanged( new ContextLoaderEvent( this, LoaderEvent.UNREGISTERED, context ));		
	}
	
	/**
	 * Get the context for the given name
	 * @param contextName
	 * @return
	 */
	public IJp2pContext getContext( String contextName ){
		for( IJp2pContext context: this.contexts ){
			if( Utils.isNull( contextName ))
				continue;
			if( context.getName().toLowerCase().equals( contextName.toLowerCase() ))
				return context;
		}
		return null;
	}

	/**
	 * Get the context for the given componentname
	 * @param contextName
	 * @return
	 */
	public IJp2pContext getContextForComponent( String contextName, String componentName ){
		if( Utils.isNull( componentName ))
			return new Jp2pContext();
		
		for( IJp2pContext context: this.contexts ){
			if(context.isValidComponentName(contextName, componentName))
				return context;
		}
		return new Jp2pContext();
	}

	/**
	 * Get the context for the given componentname
	 * @param contextName
	 * @return
	 */
	public boolean isLoadedComponent( String contextName, String componentName ){
		if( Utils.isNull( componentName ))
			return false;
		
		for( IJp2pContext context: this.contexts ){
			if(context.isValidComponentName(contextName, componentName))
				return true;
		}
		return false;
	}

	/**
	 * Load a context from the given directives
	 * @param name
	 * @param className
	 * @return
	*/
	public static IJp2pContext loadContext( Object source, String className ){
		if( Utils.isNull( className ))
			return null;
		Class<?> clss;
		IJp2pContext context = null;
		try {
			clss = source.getClass().getClassLoader().loadClass( className );
			context = (IJp2pContext) clss.newInstance();
			System.out.println("URL found: " + ( clss != null ));
		}
		catch ( Exception e1) {
			e1.printStackTrace();
		}
		return context;
	}
	
	/**
	 * Wait until the service is available
	 * @param contextName
	 * @param componentName
	 */
	public static IJp2pContext waitForService( String contextName, String componentName){
		int counter = DEFAULT_COUNT;
		while(( counter > 0 ) && !contextLoader.isLoadedComponent(contextName, componentName)){
			try{
				Thread.sleep( DEFAULT_TIME);
			}
			catch( InterruptedException ex ){
				
			}
		}
		return contextLoader.getContextForComponent(contextName, componentName);
	}
}