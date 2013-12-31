package net.osgi.jp2p.component;

import java.util.ArrayList;
import java.util.Collection;

import net.osgi.jp2p.context.AbstractServiceContainer;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public abstract class AbstractJp2pServiceNode<T extends Object>
		extends AbstractJp2pService<T> implements IJp2pComponentNode<T> {

	private IJp2pComponentNode<?> parent;
	private Collection<IJp2pComponent<?>> children;

	private ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();

	protected AbstractJp2pServiceNode( IJp2pComponentNode<?> parent, IComponentFactory<T> factory  ) {
		super( factory );
		this.children = new ArrayList<IJp2pComponent<?>>();
	}

	
	protected AbstractJp2pServiceNode(
			IJp2pWritePropertySource<IJp2pProperties> properties, T module) {
		super(properties, module);
		this.children = new ArrayList<IJp2pComponent<?>>();
	}

	protected AbstractJp2pServiceNode(String bundleId, String componentName) {
		super(bundleId, componentName);
		this.children = new ArrayList<IJp2pComponent<?>>();
	}


	@Override
	public boolean isRoot() {
		return (parent == null );
	}
	@Override
	public IJp2pComponent<?> getParent() {
		return parent;
	}

	@Override
	public void addChild( IJp2pComponent<?> child ){
		this.children.add( child );
		notifyComponentChanged( new ComponentChangedEvent( this, AbstractServiceContainer.ServiceChange.CHILD_ADDED ));
	}

	@Override
	public void removeChild( IJp2pComponent<?> child ){
		this.children.remove( child );
		notifyComponentChanged( new ComponentChangedEvent( this, AbstractServiceContainer.ServiceChange.CHILD_REMOVED ));
	}

	protected void notifyComponentChanged( ComponentChangedEvent event){
		dispatcher.serviceChanged( event );		
	}
	
	@Override
	public Collection<IJp2pComponent<?>> getChildren(){
		return this.children;
	}

	@Override
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}
}