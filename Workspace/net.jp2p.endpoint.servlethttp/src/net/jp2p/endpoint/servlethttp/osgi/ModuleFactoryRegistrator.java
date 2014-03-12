package net.jp2p.endpoint.servlethttp.osgi;


import java.util.Dictionary;

import net.jp2p.chaupal.core.AbstractRegistrator;
import net.jp2p.endpoint.servlethttp.osgi.ModuleFactory;
import net.jxse.platform.IJxtaModuleFactory;

public class ModuleFactoryRegistrator extends AbstractRegistrator<IJxtaModuleFactory> {
	
	private static final String S_MODULE_FACTORY = "ModuleFactory";


	public ModuleFactoryRegistrator() {
		super( ModuleFactory.class.getName() );
	}

	@Override
	protected IJxtaModuleFactory createRegisteredObject() {
		return new ModuleFactory();
	}

	@Override
	protected void fillDictionary(Dictionary<String, Object> dictionary) {
		dictionary.put( S_MODULE_FACTORY, super.getRegistered() );				
	}
}
