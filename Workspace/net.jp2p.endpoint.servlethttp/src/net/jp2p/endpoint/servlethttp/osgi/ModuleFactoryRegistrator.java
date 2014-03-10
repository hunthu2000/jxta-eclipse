package net.jp2p.endpoint.servlethttp.osgi;


import net.jp2p.chaupal.core.AbstractRegistrator;
import net.jp2p.endpoint.servlethttp.osgi.ModuleFactory;
import net.jxse.platform.IJxtaModuleFactory;

public class ModuleFactoryRegistrator extends AbstractRegistrator<IJxtaModuleFactory> {
	
	private static final String S_MODULE_FACTORY = "ModuleFactory";


	public ModuleFactoryRegistrator() {
		super( S_MODULE_FACTORY );
	}

	@Override
	protected IJxtaModuleFactory createRegisteredObject() {
		return new ModuleFactory();
	}
}
