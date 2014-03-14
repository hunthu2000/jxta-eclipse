package net.jp2p.endpoint.servlethttp.osgi;


import java.util.Dictionary;

import org.osgi.framework.BundleContext;

import net.jp2p.chaupal.context.AbstractRegistrator;
import net.jp2p.endpoint.servlethttp.osgi.ModuleFactory;
import net.jxse.platform.IJxtaModuleFactory;

public class ModuleFactoryRegistrator extends AbstractRegistrator<IJxtaModuleFactory> {
	
	private static final String S_MODULE_FACTORY = "ModuleFactory";


	public ModuleFactoryRegistrator( BundleContext context ) {
		super( ModuleFactory.class.getName(), context );
	}

	@Override
	protected void fillDictionary(Dictionary<String, Object> dictionary) {
		dictionary.put( S_MODULE_FACTORY, super.getRegistered() );				
	}
}
