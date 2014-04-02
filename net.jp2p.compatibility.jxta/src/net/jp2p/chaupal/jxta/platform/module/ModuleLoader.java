package net.jp2p.chaupal.jxta.platform.module;

import java.util.Collection;

import net.jp2p.chaupal.jxta.module.AbstractModuleLoader;
import net.jp2p.chaupal.jxta.module.ModuleFactoryRegistrator;
import net.jp2p.chaupal.jxta.platform.Activator;
import net.jxse.module.IJxtaModuleService;
import net.jxta.peergroup.IModuleDefinitions;
import net.jxta.platform.Module;
import net.jxta.platform.ModuleClassID;

public class ModuleLoader<T extends Module> extends AbstractModuleLoader<T> {

	@Override
	protected void addRequiredModuleClassIDs(
			Collection<ModuleClassID> requiredIds) {
		requiredIds.add( IModuleDefinitions.http2ProtoClassID );	
		requiredIds.add( IModuleDefinitions.tcpProtoClassID );	
	}

	@Override
	protected boolean onModuleServiceAdded(IJxtaModuleService<T> module) {
		ModuleFactoryRegistrator registrator = Activator.getModuleFactoryRegistrator();
		registrator.register( module );
		return true;
	}
}
