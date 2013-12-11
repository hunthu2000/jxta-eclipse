package net.osgi.jxse.component;

import java.util.ArrayList;
import java.util.Collection;

import net.osgi.jxse.component.IComponentChangedListener.ServiceChange;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;

public abstract class AbstractJxseServiceNode<T extends Object, U extends Object, V extends IJxseDirectives>
		extends AbstractJxseService<T, U, V> implements IJxseComponentNode<T,U> {

	private IJxseComponentNode<?,IJxseProperties> parent;
	private Collection<IJxseComponent<?,?>> children;

	private ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();

	protected AbstractJxseServiceNode( IJxseComponentNode<?,IJxseProperties> parent, IComponentFactory<T,U,V> factory  ) {
		super( factory );
		this.children = new ArrayList<IJxseComponent<?,?>>();
	}

	@Override
	public boolean isRoot() {
		return (parent == null );
	}
	@Override
	public IJxseComponent<?, ?> getParent() {
		return parent;
	}

	@Override
	public void addChild( IJxseComponent<?,?> child ){
		this.children.add( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, ServiceChange.CHILD_ADDED ));
	}

	@Override
	public void removeChild( IJxseComponent<?,?> child ){
		this.children.remove( child );
		dispatcher.serviceChanged( new ComponentChangedEvent( this, ServiceChange.CHILD_REMOVED ));
	}

	@Override
	public Collection<IJxseComponent<?,?>> getChildren(){
		return this.children;
	}

	@Override
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}
}