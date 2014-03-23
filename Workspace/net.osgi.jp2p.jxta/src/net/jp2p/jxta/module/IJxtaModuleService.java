package net.jp2p.jxta.module;

import net.jp2p.container.component.IModuleService;
import net.jxta.document.Advertisement;
import net.jxta.impl.protocol.PlatformConfig;
import net.jxta.platform.Module;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleImplAdvertisement;

public interface IJxtaModuleService extends IModuleService<Module>{

	/**
	 * Get a module spec ID
	 */
	public ModuleClassID getModuleClassID();

	/**
	 * Get the module spec ID for this module
	 * @return
	 */
	public ModuleSpecID getModuleSpecID();
	
	/**
	 * get a module implementation advertisement
	 * @return
	 */
	public ModuleImplAdvertisement getModuleImplAdvertisement();
	
	
	/**
	 * Get the advertisement for this module
	 * @return
	 */
	public Advertisement getAdvertisement( PlatformConfig config );
}