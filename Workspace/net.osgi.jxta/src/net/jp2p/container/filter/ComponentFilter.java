package net.jp2p.container.filter;

import net.jp2p.container.component.IJp2pComponent;


public class ComponentFilter<T extends Object> implements IFilter<IJp2pComponent<T>> {

	private String componentName;
	private boolean accepted;
	
	public ComponentFilter( String componentName ) {
		this.componentName = componentName;
		this.accepted = false;
	}
	
	@Override
	public boolean accept(IJp2pComponent<T> compoent ) {
		accepted = this.componentName.equals( compoent.getPropertySource().getComponentName() );
		return accepted;
	}

	@Override
	public boolean hasAccepted() {
		return accepted;
	}
}
