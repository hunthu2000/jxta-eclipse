package net.jp2p.chaupal.context;


import java.util.Dictionary;

import org.osgi.framework.BundleContext;

import net.jp2p.chaupal.activator.AbstractRegistrator;
import net.jp2p.container.context.IJp2pContext;

public class ContextRegistrator extends AbstractRegistrator<IJp2pContext> {
	
	private static final String S_CONTEXT_SERVICE = "ContextService";


	public ContextRegistrator( BundleContext context ) {
		super( IJp2pContext.class.getName(), context );
	}

	@Override
	protected void fillDictionary(Dictionary<String, Object> dictionary) {
		dictionary.put( S_CONTEXT_SERVICE, super.getRegistered() );				
	}

	@Override
	public void unregister() throws Exception {
		super.unregister();
	}
}
