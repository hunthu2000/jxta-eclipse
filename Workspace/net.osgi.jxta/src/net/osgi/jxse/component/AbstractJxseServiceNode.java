package net.osgi.jxse.component;

import java.util.ArrayList;
import java.util.Collection;

import net.osgi.jxse.context.AbstractServiceContext;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;

public abstract class AbstractJxseServiceNode<T extends Object, U extends Object>
		extends AbstractJxseService<T, U> implements IJxseComponentNode<T,U> {

	private IJxseComponentNode<?,IJxseProperties> parent;
	private Collection<IJxseComponent<?,?>> children;

	private ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();

	protected AbstractJxseServiceNode( IJxseComponentNode<?,IJxseProperties> parent, IComponentFactory<T,U> factory  ) {
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
		notifyComponentChanged( new ComponentChangedEvent( this, AbstractServiceContext.ServiceChange.CHILD_ADDED ));
	}

	@Override
	public void removeChild( IJxseComponent<?,?> child ){
		this.children.remove( child );
		notifyComponentChanged( new ComponentChangedEvent( this, AbstractServiceContext.ServiceChange.CHILD_REMOVED ));
	}

	protected void notifyComponentChanged( ComponentChangedEvent event){
		dispatcher.serviceChanged( event );		
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