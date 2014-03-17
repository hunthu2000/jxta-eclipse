package net.jp2p.chaupal.activator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import org.osgi.service.log.LogService;

import net.jp2p.chaupal.context.ContextServiceParser;
import net.jp2p.chaupal.context.ServiceInfo;
import net.jp2p.chaupal.xml.XMLContainerBuilder;
import net.jp2p.container.IJp2pContainer;
import net.jp2p.container.builder.IFactoryBuilder;
import net.jp2p.container.context.ContextLoader;
import net.jp2p.container.context.ContextLoaderEvent;
import net.jp2p.container.context.IContextLoaderListener;

public class Jp2pContainerBuilderHelper implements IContextLoaderListener{

	public static final String S_ERR_CONTEXT_FOUND = "The following context was found and registered: ";
	
	private ContextLoader contextLoader;

	private Collection<ServiceInfo> services;
	private Collection<IContainerBuilderListener> listeners;
	private LogService log;
	private Jp2pBundleActivator activator;
	private Collection<ContextServiceParser> parsers;

	protected Jp2pContainerBuilderHelper( Jp2pBundleActivator activator, ContextLoader contextLoader ) {
		this.contextLoader = contextLoader;
		this.activator = activator;
		this.log = activator.getLog();
		services = new ArrayList<ServiceInfo>();
		listeners = new ArrayList<IContainerBuilderListener>();
		parsers = new ArrayList<ContextServiceParser>();
	}

	public void addContainerBuilderListener( IContainerBuilderListener listener ){
		listeners.add( listener );
	}

	public void removeContainerBuilderListener( IContainerBuilderListener listener ){
		listeners.remove( listener );
	}

	private final void notifyListeners( ContainerBuilderEvent event ){
		for( IContainerBuilderListener listener: listeners )
			listener.notifyContainerBuilt(event);
	}
	
	public void open(){

		//We first parse the jp2p xml file to see which services we need
		parsers.add( new ContextServiceParser( contextLoader, activator.getClass() ));
		try {
			this.extendParsers( Jp2pContainerBuilderHelper.class );
			this.extendParsers( activator.getClass() );
		} catch (IOException e) {
			e.printStackTrace();
		}
		//We listen until all the services are available
		contextLoader.addContextLoaderListener(this);
		for(ContextServiceParser parser: parsers ){
			services.addAll( parser.parse() );
		}

	}

	public void close(){
		contextLoader.removeContextLoaderListener(this);		
	}
	
	/* (non-Javadoc)
	 * @see net.osgi.jp2p.chaupal.xml.IFactoryBuilder#isCompleted()
	 */
	private boolean isCompleted() {
		for( ServiceInfo info: services ){
			if( !info.isFound())
				return false;
		}
		return true;
	}

	/**
	 * Try to build the container. Returns false if the container cannot be built
	 */
	private boolean buildContainer(){
		if( !isCompleted())
			return false;
		XMLContainerBuilder builder = new XMLContainerBuilder( activator.getBundleId(), this.getClass(), contextLoader );
		IJp2pContainer container = builder.build();
		this.notifyListeners( new ContainerBuilderEvent(this, container));
		return true;
	}
	
	/**
	 * Allow additional builders to extend the primary builder, by looking at resources with the
	 * similar name and location, for instance provided by fragments
	 * @param clss
	 * @param containerBuilder
	 * @throws IOException
	 */
	private void extendParsers( Class<?> clss ) throws IOException{
		Enumeration<URL> enm = clss.getClassLoader().getResources( IFactoryBuilder.S_DEFAULT_LOCATION );
		while( enm.hasMoreElements()){
			URL url = enm.nextElement();
			parsers.add( new ContextServiceParser( contextLoader, url, clss ));
		}
	}
	
	@Override
	public void notifyRegisterContext(ContextLoaderEvent event) {
		log.log( LogService.LOG_INFO, S_ERR_CONTEXT_FOUND + event.getContext().getName() );
		for( ServiceInfo info: services ){
			for( String name: event.getContext().getSupportedServices() ){
				if( !info.getName().equals(name))
					continue;
				if(( info.getContext() == null ) || ( event.getContext().getName().equals( info.getContext() ))){
					info.setContext( event.getContext().getName());
					info.setFound( true );
					buildContainer();
				}
			}
		}		
	}

	@Override
	public void notifyUnregisterContext(ContextLoaderEvent event) {
		for( String name: event.getContext().getSupportedServices() ){
			for( ServiceInfo info: services ){
				if( !info.getName().equals(name))
					continue;
				if(( info.getContext() != null ) || ( event.getContext().getName().equals( info.getContext() )))
					info.setFound( false );
			}
		}
	}

}
