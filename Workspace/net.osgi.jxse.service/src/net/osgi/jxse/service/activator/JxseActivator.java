package net.osgi.jxse.service.activator;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.activator.ISimpleActivator;
import net.osgi.jxse.activator.JxseContextStarter;
import net.osgi.jxse.context.IJxseServiceContext;

public class JxseActivator implements ISimpleActivator {

	private IJxseServiceContext<NetworkManager> jxtaContext;
	private JxseContextStarter<IJxseServiceContext<NetworkManager>,NetworkManager> starter;
	
	private boolean active;
		
	public JxseActivator() {
		active = false;
	}

	void setJxtaContext(IJxseServiceContext<NetworkManager> jxtaContext) {
		this.jxtaContext = jxtaContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public boolean start(){
		try{
			starter = new JxseContextStarter<IJxseServiceContext<NetworkManager>,NetworkManager>( jxtaContext );
			starter.createContext();
			this.active = true;
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return this.active;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(){
		this.active = false;
		jxtaContext.stop();
		starter.removeContext();
	}

	public IJxseServiceContext<NetworkManager> getServiceContext(){
		return jxtaContext;
	}

	@Override
	public boolean isActive() {
		return active;
	}
}