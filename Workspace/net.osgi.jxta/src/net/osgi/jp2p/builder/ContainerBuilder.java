package net.osgi.jp2p.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class ContainerBuilder implements IContainerBuilder{

	public static final String S_WRN_NOT_COMPLETE = "\n\t!!! The Service Container did not complete: ";

	private List<ICompositeBuilderListener<?>> factories;
	
	public ContainerBuilder() {
		factories = new ArrayList<ICompositeBuilderListener<?>>();
	}


	/* (non-Javadoc)
	 * @see net.osgi.jp2p.builder.IContainerBuilder#addFactory(net.osgi.jp2p.factory.IComponentFactory)
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean addFactory( IComponentFactory<?> factory ){
		boolean retval = this.factories.add( factory );
		Collections.sort(this.factories, new FactoryComparator());
		return retval;
	}

	/* (non-Javadoc)
	 * @see net.osgi.jp2p.builder.IContainerBuilder#removeFactory(net.osgi.jp2p.factory.IComponentFactory)
	 */
	@Override
	public boolean removeFactory( IComponentFactory<Object> factory ){
		return this.factories.remove( factory );
	}
	
	@SuppressWarnings("unchecked")
	public IComponentFactory<Object>[] getFactories(){
		return this.factories.toArray( new IComponentFactory[0] );
	}
	
	/* (non-Javadoc)
	 * @see net.osgi.jp2p.builder.IContainerBuilder#getFactory(java.lang.String)
	 */
	@Override
	public IComponentFactory<?> getFactory( String name ){
		for( ICompositeBuilderListener<?> listener: factories ){
			IComponentFactory<?> factory = (IComponentFactory<?>) listener;
			if( factory.getComponentName().equals(name ))
					return factory;
		}
		return null;	
	}

	/* (non-Javadoc)
	 * @see net.osgi.jp2p.builder.IContainerBuilder#getFactory(net.osgi.jp2p.properties.IJp2pPropertySource)
	 */
	@Override
	public IComponentFactory<?> getFactory( IJp2pPropertySource<?> source ){
		for( ICompositeBuilderListener<?> listener: factories ){
			IComponentFactory<?> factory = (IComponentFactory<?>) listener;
			if( factory.getPropertySource().equals( source ))
					return factory;
		}
		return null;	
	}

	/* (non-Javadoc)
	 * @see net.osgi.jp2p.builder.IContainerBuilder#isCompleted()
	 */
	@Override
	public boolean isCompleted(){
		for( ICompositeBuilderListener<?> listener: factories ){
			IComponentFactory<?> factory = (IComponentFactory<?>) listener;
			if( !factory.isCompleted())
				return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.osgi.jp2p.builder.IContainerBuilder#listModulesNotCompleted()
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see net.osgi.jp2p.builder.IContainerBuilder#updateRequest(net.osgi.jp2p.factory.ComponentBuilderEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public synchronized void updateRequest(ComponentBuilderEvent<?> event) {
		for( ICompositeBuilderListener<?> listener: this.factories ){
			if( !listener.equals( event.getFactory())){
				IComponentFactory<Object> factory = (IComponentFactory<Object>) listener;
				factory.notifyChange( (ComponentBuilderEvent<Object>) event);
			}
		}
	}	
	

	/* (non-Javadoc)
	 * @see net.osgi.jp2p.builder.IContainerBuilder#addFactoryToContainer(java.lang.String, net.osgi.jp2p.properties.IJp2pPropertySource, boolean, boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public IComponentFactory<?> addFactoryToContainer( String componentName, IJp2pPropertySource<IJp2pProperties> parentSource, boolean createSource, boolean blockCreation) {
		String str = StringStyler.styleToEnum(componentName);
		IJp2pWritePropertySource<IJp2pProperties> ncp = (IJp2pWritePropertySource<IJp2pProperties>) parentSource.getChild( str );
		if( ncp != null )
			return null;
		IComponentFactory<?> ncf = Jp2pContext.getDefaultFactory( this, parentSource, componentName );
		addFactory(ncf);
		if(!createSource )
			return ncf;
		IJp2pWritePropertySource<IJp2pProperties> props = (IJp2pWritePropertySource<IJp2pProperties>) ncf.createPropertySource();
		props.setDirective( Directives.BLOCK_CREATION, Boolean.valueOf( blockCreation ).toString());
		parentSource.addChild(props);
		return ncf; 
	}

	/* (non-Javadoc)
	 * @see net.osgi.jp2p.builder.IContainerBuilder#getOrCreateChildFactory(net.osgi.jp2p.properties.IJp2pPropertySource, java.lang.String, boolean, boolean)
	 */
	@Override
	public IComponentFactory<?> getOrCreateChildFactory( IJp2pPropertySource<IJp2pProperties> source, String componentName, boolean createSource, boolean blockCreation ){
		IJp2pPropertySource<?> child = source.getChild( componentName ); 
		if( child != null )
			return this.getFactory(child );
		return addFactoryToContainer(componentName, source, createSource, blockCreation);
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