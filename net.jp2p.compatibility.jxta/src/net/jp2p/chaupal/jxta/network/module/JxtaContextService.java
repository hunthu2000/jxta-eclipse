package net.jp2p.chaupal.jxta.network.module;

import org.osgi.framework.BundleContext;

import net.jp2p.chaupal.context.ContextRegistrator;
import net.jp2p.chaupal.jxta.module.AbstractService;
import net.jp2p.container.context.IJp2pContext;
import net.jp2p.container.service.IServiceListener;
import net.jp2p.container.service.ServiceListenerEvent;
import net.jp2p.jxta.context.JxtaContextUtils;
import net.jp2p.network.jxta.context.JxtaNetworkContext;
import net.jxta.peergroup.IModuleDefinitions.DefaultModules;
import net.jxta.platform.ModuleClassID;

public class JxtaContextService extends AbstractService<IJp2pContext> {

	private static String filter = "(jp2p.context=*)"; //in component.xml file you will use target="(jp2p.context=contextName)"

    private static int amount;
    
	public JxtaContextService(BundleContext bc) {
		super(bc, IJp2pContext.class, filter);
		super.addServiceEventListeners( new ServiceListener( bc ));
		DefaultModules[] classIds = DefaultModules.values();
		amount = classIds.length;
	}

	private static class ServiceListener implements IServiceListener<IJp2pContext>{

		private BundleContext bc;
		
		protected ServiceListener( BundleContext bc ) {
			super();
			this.bc = bc;
		}

		@Override
		public void notifyModuleServiceChanged(ServiceListenerEvent<IJp2pContext> event) {
			IJp2pContext service = event.getData();
			ModuleClassID[] classIds = JxtaContextUtils.getLoadedModuleClassIDs( service );	
			if(( classIds == null ) || ( classIds.length < amount ))
				return;
			ContextRegistrator cr = new ContextRegistrator( bc ); 
			cr.register( new JxtaNetworkContext() );	
		}
		
	}
}
