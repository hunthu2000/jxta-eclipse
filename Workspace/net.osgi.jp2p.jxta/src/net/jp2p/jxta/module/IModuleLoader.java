package net.jp2p.jxta.module;

import net.jxse.module.IJxtaModuleService;
import net.jxta.platform.Module;

public interface IModuleLoader<T extends Module> {

	public boolean addModule( IJxtaModuleService<T> module );
}
