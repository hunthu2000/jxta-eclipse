package net.jp2p.jxta.context;

import net.jp2p.container.context.IJp2pContext;
import net.jxta.platform.ModuleClassID;

public interface IJxtaContext extends IJp2pContext {

	/**
	 * Get the names of the module class ids that are supported by this context
	 * @return
	 */
	public ModuleClassID[] getSupportedModuleClassIDs();
}
