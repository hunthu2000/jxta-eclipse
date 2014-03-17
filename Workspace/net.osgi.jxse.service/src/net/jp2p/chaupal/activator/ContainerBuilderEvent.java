package net.jp2p.chaupal.activator;

import java.util.EventObject;

import net.jp2p.container.IJp2pContainer;

public class ContainerBuilderEvent extends EventObject {
	private static final long serialVersionUID = -1266257260044093122L;

	private IJp2pContainer container;
	
	public ContainerBuilderEvent(Object source, IJp2pContainer container) {
		super(source);
	}

	public IJp2pContainer getContainer() {
		return container;
	}
}
