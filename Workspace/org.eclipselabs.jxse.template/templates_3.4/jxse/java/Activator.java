package $packageName$;

import net.osgi.jxse.service.activator.JxseBundleActivator;
import org.osgi.framework.*;

public class Activator extends JxseBundleActivator {

	public static final String S_PLUGIN_ID = "$packageName$";
	
	private static JxseBundleActivator activator;

	public Activator() {
		super( S_PLUGIN_ID);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		super.setObserver( new ContextObserver() );
		super.start(bundleContext);
		activator = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		super.stop(bundleContext);
		activator = null;
	}
	
	public static JxseBundleActivator getDefault(){
		return activator;
	}
}