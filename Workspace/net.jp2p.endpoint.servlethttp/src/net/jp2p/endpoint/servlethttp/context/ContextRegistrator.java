package net.jp2p.endpoint.servlethttp.context;


import java.util.Dictionary;

import net.jp2p.chaupal.core.AbstractRegistrator;
import net.jp2p.container.context.IJp2pContext;

public class ContextRegistrator extends AbstractRegistrator<IJp2pContext> {
	
	public ContextRegistrator() {
		super( IJp2pContext.class.getName() );
	}

	@Override
	protected IJp2pContext createRegisteredObject() {
		return new HttpContext();
	}

	@Override
	protected void fillDictionary(Dictionary<String, Object> dictionary) {
		dictionary.put( super.getIdentifier(), super.getRegistered() );						
	}
}
