package net.osgi.jp2p.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.factory.IComponentFactory.Components;
import net.osgi.jp2p.log.LoggerFactory;
import net.osgi.jp2p.partial.PartialFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;
import net.osgi.jp2p.startup.StartupServiceFactory;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class ContainerBuilder{

	public static final String S_WRN_NOT_COMPLETE = "\n\t!!! The Service Container did not complete: ";

	private List<ICompositeBuilderListener<?>> factories;
	
	
	public ContainerBuilder() {
		factories = new ArrayList<ICompositeBuilderListener<?>>();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean addFactory( IComponentFactory<?> factory ){
		boolean retval = this.factories.add( factory );
		Collections.sort(this.factories, new FactoryComparator());
		return retval;
	}

	public boolean removeFactory( IComponentFactory<Object> factory ){
		return this.factories.remove( factory );
	}
	
	@SuppressWarnings("unchecked")
	public IComponentFactory<Object>[] getChildren(){
		return this.factories.toArray( new IComponentFactory[0] );
	}
	
	public IComponentFactory<?> getFactory( String name ){
		for( ICompositeBuilderListener<?> listener: factories ){
			IComponentFactory<?> factory = (IComponentFactory<?>) listener;
			if( factory.getComponentName().equals(name ))
					return factory;
		}
		return null;	
	}

	/**
	 * Returns true if all the factorys have been completed
	 * @return
	 */
	public boolean isCompleted(){
		for( ICompositeBuilderListener<?> listener: factories ){
			IComponentFactory<?> factory = (IComponentFactory<?>) listener;
			if( !factory.isCompleted())
				return false;
		}
		return true;
	}
	
	/**
	 * List the factorys that did not complete
	 * @return
	 */
	public String listModulesNotCompleted(){
		StringBuffer buffer = new StringBuffer();
		for( ICompositeBuilderListener<?> listener: factories ){
			IComponentFactory<?> factory = (IComponentFactory<?>) listener;
			if( !factory.isCompleted())
				buffer.append( "\t\t\t"+ factory.getComponentName() + "\n" );
		}
		if( Utils.isNull( buffer.toString() ))
			return null;
		return S_WRN_NOT_COMPLETE + buffer.toString();
	}
	
	/**
	 * Perform a request for updating the container
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public synchronized void updateRequest(ComponentBuilderEvent<?> event) {
		for( ICompositeBuilderListener<?> listener: this.factories ){
			if( !listener.equals( event.getFactory())){
				IComponentFactory<Object> factory = (IComponentFactory<Object>) listener;
				factory.notifyChange( (ComponentBuilderEvent<Object>) event);
			}
		}
	}	
	
	/**
	 * Get the default factory for this container
	 * @param parent
	 * @param componentName
	 * @return
	 */
	public IComponentFactory<?> getDefaultFactory( IJp2pPropertySource<IJp2pProperties> parentSource, String componentName ){
		if( Utils.isNull(componentName))
			return null;
		String comp = StringStyler.styleToEnum(componentName);
		if( !Components.isComponent( comp ))
			return null;
		Components component = Components.valueOf(comp);
		IComponentFactory<?> factory = null;
		switch( component ){
		case STARTUP_SERVICE:
			factory = new StartupServiceFactory( this, parentSource );
			break;
		case TCP:
		case HTTP:
		case HTTP2:
		case MULTICAST:
		case SECURITY:
			factory = new PartialFactory<Object>( this, componentName, parentSource );
			break;
		case LOGGER_SERVICE:
			factory = new LoggerFactory( this, parentSource );
			break;
		default:
			break;
		}
		return factory;
	}

	/** Add a factory with the given component name to the container. use the given component name and parent,
	 * if 'createsource' is true, the property source is immediately created, and 'blockcreation' means that
	 * the builder will not create the factory. instead the parent factory should provide for this 
	 * 
	 * @param componentName
	 * @param createSource
	 * @param blockCreation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IComponentFactory<?> addFactoryToContainer( String componentName, IComponentFactory<?> parent, boolean createSource, boolean blockCreation) {
		String str = StringStyler.styleToEnum(componentName);
		IJp2pWritePropertySource<IJp2pProperties> ncp = (IJp2pWritePropertySource<IJp2pProperties>) parent.getPropertySource().getChild( str );
		if( ncp != null )
			return null;
		IComponentFactory<?> ncf = getDefaultFactory( parent.getPropertySource(), componentName );
		addFactory(ncf);
		if(!createSource )
			return ncf;
		IJp2pWritePropertySource<IJp2pProperties> props = (IJp2pWritePropertySource<IJp2pProperties>) ncf.createPropertySource();
		props.setDirective( Directives.BLOCK_CREATION, Boolean.valueOf( blockCreation ).toString());
		parent.getPropertySource().addChild(props);
		return ncf; 
	}

}

/**
 * This comparator comparse the weights of the factorys
 * @author Kees
 *
 */
class FactoryComparator<T extends Object> implements Comparator<ICompositeBuilderListener<T>>{

	@Override
	public int compare(ICompositeBuilderListener<T> arg0, ICompositeBuilderListener<T> arg1) {
		if(!(arg0 instanceof IComponentFactory<?>))
			return -1;
		if(!(arg1 instanceof IComponentFactory<?>))
			return 1;
		IComponentFactory<?> f0 = (IComponentFactory<?>) arg0;
		IComponentFactory<?> f1 = (IComponentFactory<?>) arg1;
		return f0.getWeight() - f1.getWeight();
	}
}