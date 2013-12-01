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
package net.osgi.jxse.service.activator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.context.IJxseServiceContext;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.log.JxseLevel;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractJxseBundleActivator implements BundleActivator {

	private static final String S_MSG_NOT_A_JXSE_BUNDLE = "\n\nThis bundle is not a valid JXSE Bundle. A JXSE-INF directory is required!\n\n";
	private static final String S_JXSE_INF = "/JXSE-INF";
	private static final String S_MSG_LOG = "Logging at JXSE LEVEL!!!!";
	
	private JxseActivator jxtaActivator;
	private ServiceTracker<BundleContext,LogService> logServiceTracker;
	private LogService logService;
		
	/**
	 * Create the context
	 * @return
	 */
	protected abstract IJxseServiceContext<NetworkManager, ContextProperties> createContext();
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		if(this.getClass().getResource( S_JXSE_INF ) == null )
			Logger.getLogger( this.getClass().getName() ).warning( S_MSG_NOT_A_JXSE_BUNDLE);
		
		Level level = JxseLevel.getJxtaLevel();
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

		ClassLoader parentClassLoader = this.getClass().getClassLoader();
	    MyClassLoader classLoader = new MyClassLoader(parentClassLoader);
	    classLoader.loadClass( JxseActivator.class.getCanonicalName() );

		jxtaActivator = new JxseActivator();
		jxtaActivator.setJxtaContext( this.createContext() );
		jxtaActivator.start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		if( jxtaActivator != null )
			jxtaActivator.stop();
		if(logService != null)
			logService.log(LogService.LOG_INFO, "Logging service Stopped");
		
		// close the service tracker
		logServiceTracker.close();
		logServiceTracker = null;
	}

	public IJxseServiceContext<NetworkManager, ContextProperties> getServiceContext(){
		return jxtaActivator.getServiceContext();
	}
}

class MyClassLoader extends ClassLoader{

	private static String S_NETWORK_MANAGER = "net.jxta.platform.NetworkManager";
	
    public MyClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class loadClass(String name) throws ClassNotFoundException {
        if(!S_NETWORK_MANAGER.equals(name))
                return super.loadClass(name);

        try {
        	String url = "file:C:/data/projects/tutorials/web/WEB-INF/" +
                            "classes/reflection/MyObject.class";
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data = input.read();

            while(data != -1){
                buffer.write(data);
                data = input.read();
            }

            input.close();

            byte[] classData = buffer.toByteArray();

            return defineClass( S_NETWORK_MANAGER, classData, 0, classData.length);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace(); 
        }

        return null;
    }
}