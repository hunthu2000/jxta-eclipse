package net.osgi.jxse.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.osgi.jxse.advertisement.JxseAdvertisementFactory;
import net.osgi.jxse.discovery.DiscoveryServiceFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.log.LoggerFactory;
import net.osgi.jxse.netpeergroup.NetPeerGroupFactory;
import net.osgi.jxse.network.NetworkManagerFactory;
import net.osgi.jxse.network.configurator.NetworkConfigurationFactory;
import net.osgi.jxse.partial.PartialFactory;
import net.osgi.jxse.peergroup.PeerGroupFactory;
import net.osgi.jxse.pipe.PipeServiceFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.registration.RegistrationServiceFactory;
import net.osgi.jxse.seeds.SeedListFactory;
import net.osgi.jxse.startup.StartupServiceFactory;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class BuilderContainer{

	public static final String S_WRN_NOT_COMPLETE = "\n\t!!! The Service Container did not complete: ";

	private List<ICompositeBuilderListener<?>> factories;
	
	
	public BuilderContainer() {
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
	public IComponentFactory<?> getDefaultFactory( IJxsePropertySource<IJxseProperties> parentSource, String componentName ){
		if(( Utils.isNull( componentName ) || ( !Components.isComponent( componentName ))))
			return null;
		Components component = Components.valueOf(componentName);
		IComponentFactory<?> factory = null;
		switch( component ){
		case STARTUP_SERVICE:
			factory = new StartupServiceFactory( this, parentSource );
			break;
		case NETWORK_MANAGER:
			factory = new NetworkManagerFactory( this, parentSource  );
			break;
		case NETWORK_CONFIGURATOR:
			factory = new NetworkConfigurationFactory( this, parentSource );
			break;
		case SEED_LIST:
			factory = new SeedListFactory( this, parentSource );
			break;
		case TCP:
		case HTTP:
		case HTTP2:
		case MULTICAST:
		case SECURITY:
			factory = new PartialFactory<Object>( this, componentName, parentSource );
			break;
		case NET_PEERGROUP_SERVICE:
			factory = new NetPeerGroupFactory( this, parentSource );
			break;			
		case PIPE_SERVICE:
			factory = new PipeServiceFactory( this, parentSource );
			break;			
		case REGISTRATION_SERVICE:
			factory = new RegistrationServiceFactory( this, parentSource );
			break;
		case DISCOVERY_SERVICE:
			factory = new DiscoveryServiceFactory( this, parentSource );
			break;			
		case PEERGROUP_SERVICE:
			factory = new PeerGroupFactory( this, parentSource );
			break;			
		case ADVERTISEMENT_SERVICE:
			factory = new JxseAdvertisementFactory( this, parentSource );
			break;
		case LOGGER_SERVICE:
			factory = new LoggerFactory( this, parentSource );
			break;
		default:
			break;
		}
		return factory;
	}

	/** Add a factory with the given component name to the container 
	 * 
	 * @param componentName
	 * @param createSource
	 * @param blockCreation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IComponentFactory<?> addFactoryToContainer( String componentName, IComponentFactory<?> parent, boolean createSource, boolean blockCreation) {
		String str = StringStyler.styleToEnum(componentName);
		IJxseWritePropertySource<IJxseProperties> ncp = (IJxseWritePropertySource<IJxseProperties>) parent.getPropertySource().getChild( str );
		if( ncp != null )
			return null;
		IComponentFactory<?> ncf = getDefaultFactory( parent.getPropertySource(), str );
		addFactory(ncf);
		if(!createSource )
			return ncf;
		IJxseWritePropertySource<IJxseProperties> props = (IJxseWritePropertySource<IJxseProperties>) ncf.createPropertySource();
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