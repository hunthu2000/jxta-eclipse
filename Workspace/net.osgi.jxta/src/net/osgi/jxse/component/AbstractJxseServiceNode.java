package net.osgi.jxse.component;

import java.util.ArrayList;
import java.util.Collection;

import net.osgi.jxse.context.AbstractServiceContext;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public abstract class AbstractJxseServiceNode<T extends Object>
		extends AbstractJxseService<T> implements IJxseComponentNode<T> {

	private IJxseComponentNode<?> parent;
	private Collection<IJxseComponent<?>> children;

	private ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();

	protected AbstractJxseServiceNode( IJxseComponentNode<?> parent, IComponentFactory<T> factory  ) {
		super( factory );
		this.children = new ArrayList<IJxseComponent<?>>();
	}

	
	protected AbstractJxseServiceNode(
			IJxseWritePropertySource<IJxseProperties> properties, T module) {
		super(properties, module);
		this.children = new ArrayList<IJxseComponent<?>>();
	}

	protected AbstractJxseServiceNode(String bundleId, String componentName) {
		super(bundleId, componentName);
		this.children = new ArrayList<IJxseComponent<?>>();
	}


	@Override
	public boolean isRoot() {
		return (parent == null );
	}
	@Override
	public IJxseComponent<?> getParent() {
		return parent;
	}

	@Override
	public void addChild( IJxseComponent<?> child ){
		this.children.add( child );
		notifyComponentChanged( new ComponentChangedEvent( this, AbstractServiceContext.ServiceChange.CHILD_ADDED ));
	}

	@Override
	public void removeChild( IJxseComponent<?> child ){
		this.children.remove( child );
		notifyComponentChanged( new ComponentChangedEvent( this, AbstractServiceContext.ServiceChange.CHILD_REMOVED ));
	}

	protected void notifyComponentChanged( ComponentChangedEvent event){
		dispatcher.serviceChanged( event );		
	}
	
	@Override
	public Collection<IJxseComponent<?>> getChildren(){
		return this.children;
	}

	@Override
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}
}