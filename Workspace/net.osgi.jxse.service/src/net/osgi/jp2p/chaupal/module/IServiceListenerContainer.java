package net.osgi.jp2p.chaupal.module;

import net.jxse.osgi.module.IJp2pServiceListener;

public interface IServiceListenerContainer{

	/**
	 * Clear the factory
	 */
	public void clear();

	public boolean addListener( IJp2pServiceListener listener );
	
	public void removeListener( IJp2pServiceListener listener );
	
	public IJp2pServiceListener getListener( String componentName );
}