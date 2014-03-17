package net.jp2p.container.context;

import java.util.EventObject;

public class ContextLoaderEvent extends EventObject {
	private static final long serialVersionUID = -7007824760022520347L;

	public enum LoaderEvent{
		REGISTERED,
		UNREGISTERED;
	}
	
	private LoaderEvent type;
	private IJp2pContext context;
	
	public ContextLoaderEvent( Object source, LoaderEvent type, IJp2pContext context) {
		super(source);
		this.type = type;
		this.context = context;
	}

	public LoaderEvent getType() {
		return type;
	}

	public IJp2pContext getContext() {
		return context;
	}
}
