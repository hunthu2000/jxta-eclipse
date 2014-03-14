package net.jp2p.chaupal.context;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;

import net.jp2p.container.context.ContextLoader;

public class ContextRegistrator extends AbstractRegistrator<ContextLoader> {
	
	public ContextRegistrator( BundleContext context ) {
		super( ContextLoader.class.getName(), context );
	}
	
	@Override
	protected void fillDictionary(Dictionary<String, Object> dictionary) {
		dictionary.put( super.getIdentifier(), super.getRegistered() );						
	}
}