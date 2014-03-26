package net.jp2p.chaupal.jxta.module;

import net.jp2p.jxta.module.IJxtaModuleService;
import net.jxta.platform.Module;

public interface IModuleFactory<U extends Module>{

	/**
	 * Get the modules that are needed to start this service
	 * @return
	 */
	public IJxtaModuleService<U>[] createModules();
}
