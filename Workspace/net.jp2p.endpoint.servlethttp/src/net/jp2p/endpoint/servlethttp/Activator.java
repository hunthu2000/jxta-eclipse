package net.jp2p.endpoint.servlethttp;

import net.jp2p.endpoint.servlethttp.osgi.ModuleFactory;
import net.jxse.module.IModuleFactory;
import net.jxse.osgi.module.AbstractJp2pServiceListener;
import net.jxse.osgi.module.IJp2pServiceListener;
import net.jxta.platform.Module;
import net.jxta.platform.ModuleSpecID;
import net.osgi.jp2p.chaupal.module.AbstractModuleListenerService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private ModuleListenerService service;
	
	@Override
	public void start(BundleContext context) throws Exception {
		service = new ModuleListenerService( context );
		service.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		service.close();
	}

	private class ModuleListenerService extends AbstractModuleListenerService{

		public ModuleListenerService(BundleContext bc) {
			super(bc);
		}

		@Override
		protected IJp2pServiceListener createServiceListener() {
			return new Jp2pServiceListener();
		}
		
		/**
		 * Implement the service listener
		 * @author Kees
		 *
		 */
		private class Jp2pServiceListener extends AbstractJp2pServiceListener{
			Jp2pServiceListener() {
				super(ModuleFactory.S_HTTP_SERVICE);
			}

			@Override
			protected IModuleFactory<Module, ModuleSpecID> createFactory() {
				return new ModuleFactory();
			}
		}
	}
}
