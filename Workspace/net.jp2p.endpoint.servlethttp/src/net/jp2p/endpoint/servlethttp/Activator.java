package net.jp2p.endpoint.servlethttp;


import net.jp2p.endpoint.servlethttp.osgi.ModuleFactory;
import net.jxse.osgi.module.AbstractModuleActivator;
import net.jxse.platform.IJxtaModuleFactory;


public class Activator extends AbstractModuleActivator {

	@Override
	protected IJxtaModuleFactory createFactory() {
		return new ModuleFactory();
	}
}
