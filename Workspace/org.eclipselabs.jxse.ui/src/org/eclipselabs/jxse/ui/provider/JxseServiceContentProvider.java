package org.eclipselabs.jxse.ui.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.osgi.jxse.activator.IJxseService;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.IJxseComponentNode;
import net.osgi.jxse.component.JxseComponent;
import net.osgi.jxse.service.comparator.JxtaServiceComparator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipselabs.jxse.ui.component.PeerGroupComponent;

public class JxseServiceContentProvider implements ITreeContentProvider {

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(( parentElement == null ) || !( parentElement instanceof IJxseComponent<?,?> ))
			return null;
		IJxseComponent<?,?> decorator = (IJxseComponent<?,?>)parentElement;
		return getDecoratedChildren( decorator );
	}

	@Override
	public boolean hasChildren(Object element) {
		if(( element == null ) || !( element instanceof IJxseComponent<?,?> ))
			return false;
		IJxseComponent<?,?> decorator = (IJxseComponent<?,?>)element;
		Object[] children = getChildren( decorator );
		return ( children == null )? false: ( children.length > 0 );
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(( inputElement == null ) || !( inputElement instanceof IJxseComponent<?,?> ))
			return null;
		IJxseComponent<?,?> decorator = (IJxseComponent<?,?>)inputElement;
		if( decorator.getModule() instanceof NetworkConfigurator ){
			ITreeContentProvider provider = new NetworkConfiguratorContentProvider();
			return provider.getElements(inputElement);
		}
		return getChildren( inputElement );
	}

	@Override
	public Object getParent(Object element) {
		if(!( element instanceof IJxseComponentNode ))
			return null;
		IJxseComponentNode<?,?> decorator = (IJxseComponentNode<?,?>)element;
		if( decorator.getModule() instanceof NetworkConfigurator ){
			ITreeContentProvider provider = new NetworkConfiguratorContentProvider( decorator.getParent() );
			return provider.getParent(element);
		}
		return decorator.getParent();
	}
	
	/**
	 * Get the correct provider
	 * @param component
	 * @return
	 */
	public static NetworkConfiguratorContentProvider getNetworkConfiguratorContentProvider( IJxseComponent<?,?> component ){
		if(!( component.getModule() instanceof NetworkConfigurator ))
			return null;
		Object parent = null;
		if( component instanceof IJxseComponentNode ){
			IJxseComponentNode<?,?> node = (IJxseComponentNode<?,?> )component;
			parent = node.getParent();	
		}
		return new NetworkConfiguratorContentProvider( parent );
	}

	/**
	 * Get the correct provider
	 * @param component
	 * @return
	 */
	public static Object[] getChildren( IJxseComponent<?,?> component ){
		NetworkConfiguratorContentProvider provider = getNetworkConfiguratorContentProvider(component);
		if( provider != null ){
			return provider.getChildren( component );
		}
		if(!( component instanceof IJxseComponentNode ))
			return null;
		IJxseComponentNode<?,?> node = (IJxseComponentNode<?,?> )component;
		return node.getChildren().toArray();
	}

	/**
	 * This helper method decorates a compoent;
	 * 1: If the component is node, then nothing changes.
	 * 2: If the component is not a node, then the module determines
	 *    whether additional children are added
	 * @param element
	 * @return
	 */
	public static Object decorateComponent( Object element ){
		if( element instanceof IJxseComponentNode )
			return element;
		if(!( element instanceof IJxseComponent<?,?> ))
			return getComponent( element );
		if( element instanceof IJxseService<?,?> )
			return element;
		IJxseComponent<?,?> component = (IJxseComponent<?,?> )element;
		return getComponent( component.getModule() );
	}

	/**
	 * Returns the most adequate component for the given 
	 * @param module
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IJxseComponent<?,?> getComponent( Object module ){
		if( module instanceof IJxseComponent )
			return (IJxseComponent<?,?>) module;
		if( module instanceof PeerGroup )
			return new PeerGroupComponent( (PeerGroup) module );
		return new JxseComponent( module );
	}

	public static Object[] getDecoratedChildren( IJxseComponent<?,?> component ) {
		List<Object> results = new ArrayList<Object>();
		Object[] children = getChildren( component );
		if(( children == null ) || ( children.length == 0 ))
			return children;
		for( Object child: children )
			results.add( decorateComponent( child ));
		Collections.sort( results, new JxtaServiceComparator<Object>());
		return results.toArray();
	}
}