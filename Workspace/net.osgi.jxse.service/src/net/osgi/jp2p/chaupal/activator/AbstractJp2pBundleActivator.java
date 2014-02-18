/*******************************************************************************
 * Copyright (c) 2013 Condast and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kees Pieters - initial API and implementation
 *******************************************************************************/
package net.osgi.jp2p.chaupal.activator;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.jp2p.container.IJp2pContainer;
import net.jp2p.container.log.Jp2pLevel;
import net.jxse.module.IModuleContainer;
import net.jxse.platform.JxtaModuleContainer;
import net.jxta.platform.Module;
import net.jxta.platform.ModuleSpecID;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractJp2pBundleActivator<T extends Object> implements BundleActivator {

	private static final String S_MSG_NOT_A_JP2P_BUNDLE = "\n\nThis bundle is not a valid JP2P Bundle. A JP2P-INF directory is required!\n\n";
	private static final String S_JP2P_INF = "/JP2P-INF";
	private static final String S_MSG_LOG = "Logging at JXSE LEVEL!!!!";
	
	private Jp2pActivator<T> jp2pActivator;
	private ServiceTracker<BundleContext,LogService> logServiceTracker;
	private LogService logService;
		
	/**
	 * Create the context
	 * @return
	 */
	protected abstract IJp2pContainer<T> createContainer();
	
	protected IModuleContainer<Module, ModuleSpecID> moduleContainer;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		if(this.getClass().getResource( S_JP2P_INF ) == null )
			Logger.getLogger( this.getClass().getName() ).warning( S_MSG_NOT_A_JP2P_BUNDLE);
		
		moduleContainer = JxtaModuleContainer.getInstance();
		
		Level level = Jp2pLevel.getJxtaLevel();
		Logger log = Logger.getLogger( this.getClass().getName() );
		log.log( level, S_MSG_LOG );
		// create a tracker and track the log service
		logServiceTracker = 
				new ServiceTracker<BundleContext,LogService>(bundleContext, LogService.class.getName(), null);
		logServiceTracker.open();

		// grab the service
		logService = logServiceTracker.getService();

		if(logService != null)
			logService.log(LogService.LOG_INFO, "Logging service started");

		jp2pActivator = new Jp2pActivator<T>();
		jp2pActivator.setJxtaContext( this.createContainer() );
		jp2pActivator.start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		moduleContainer = JxtaModuleContainer.getInstance();
		moduleContainer.clear();
		
		if( jp2pActivator != null )
			jp2pActivator.stop();
		if(logService != null)
			logService.log(LogService.LOG_INFO, "Logging service Stopped");
		
		// close the service tracker
		logServiceTracker.close();
		logServiceTracker = null;
	}

	public IJp2pContainer<?> getServiceContainer(){
		return jp2pActivator.getServiceContext();
	}
}