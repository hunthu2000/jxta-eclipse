package net.jp2p.chaupal.jxta.module;


import java.util.Dictionary;

import org.osgi.framework.BundleContext;

import net.jp2p.chaupal.context.AbstractRegistrator;
import net.jp2p.container.component.IModuleService;

public class ModuleFactoryRegistrator extends AbstractRegistrator<IModuleService<?>> {
	
	private static final String S_MODULE_SERVICE = "ModuleService";


	public ModuleFactoryRegistrator( BundleContext context ) {
		super( IModuleService.class.getName(), context );
	}

	@Override
	protected void fillDictionary(Dictionary<String, Object> dictionary) {
		dictionary.put( S_MODULE_SERVICE, super.getRegistered() );				
	}
}
