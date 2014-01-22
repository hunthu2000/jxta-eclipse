package net.jp2p.container.component;

import java.util.ArrayList;
import java.util.Collection;

import net.jp2p.container.AbstractServiceContainer;
import net.jp2p.container.factory.IComponentFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pWritePropertySource;

public abstract class AbstractJp2pServiceNode<T extends Object>
		extends AbstractJp2pService<T> implements IJp2pComponentNode<T> {

	private Collection<IJp2pComponent<?>> children;

	private ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();

	protected AbstractJp2pServiceNode( IJp2pComponentNode<?> source, IComponentFactory<T> factory  ) {
		super( factory );
		this.children = new ArrayList<IJp2pComponent<?>>();
	}

	
	protected AbstractJp2pServiceNode(
			IJp2pWritePropertySource<IJp2pProperties> source, T module) {
		super(source, module);
		this.children = new ArrayList<IJp2pComponent<?>>();
	}

	protected AbstractJp2pServiceNode(String bundleId, String componentName) {
		super(bundleId, componentName);
		this.children = new ArrayList<IJp2pComponent<?>>();
	}


	@Override
	public boolean isRoot() {
		return (super.getPropertySource().getParent() == null );
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