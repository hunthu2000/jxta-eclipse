package net.jp2p.endpoint.servlethttp.context;


import net.jp2p.chaupal.core.AbstractRegistrator;
import net.jp2p.container.context.IJp2pContext;

public class ContextRegistrator extends AbstractRegistrator<IJp2pContext> {
	
	public ContextRegistrator() {
		super( HttpContext.S_HTTP_CONTEXT );
	}

	@Override
	protected IJp2pContext createRegisteredObject() {
		return new HttpContext();
	}
}
