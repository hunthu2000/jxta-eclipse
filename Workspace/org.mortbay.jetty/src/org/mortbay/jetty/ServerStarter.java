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
package org.mortbay.jetty;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxse.osgi.service.IServerStarter;
import net.jxta.logging.Logging;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpHandler;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.util.InetAddrPort;
import org.mortbay.util.Log;
import org.mortbay.util.LoggerLogSink;

public class ServerStarter implements IServerStarter {

	private static final String S_ERR_INVALID_PORT = "The required port is invalid. Should be larger than zero";

    /**
     * the relative URI of where the message receiver servlet will be mounted.
     */
    private final static String MSG_RECEIVER_RELATIVE_URI = "/*";

    /**
     *  The min threads that the HTTP server will use for handling requests.
     */
    private static int MIN_LISTENER_THREADS = 10;

    /**
     *  The max threads that the HTTP server will use for handling requests.
     */
    private static int MAX_LISTENER_THREADS = 200;

    /**
     * The number of milliseconds in a second.
     */
    public static final long TIME_SECOND = 1000;

    /**
     *  How long a thread can remain idle until the worker thread is let go.
     */
    private static long MAX_THREAD_IDLE_DURATION = 30 * TIME_SECOND;

    /**
     *  The Jetty HTTP Server instance.
     */
    private HttpServer server;
    private ServletHandler handler;
    private SocketListener listener;
    private boolean started;
    
    private Map<Object, ServletHandler> handlers;
    
    /**
     *  logger
     */
    private final static transient Logger LOG = Logger.getLogger(ServerStarter.class.getName());

    private Collection<SocketListener> socketListeners;
    private static IServerStarter starter = new ServerStarter();
    
    public ServerStarter()
    {
    	this.started = false;
    	handlers = new HashMap<Object, ServletHandler>();
    	this.socketListeners = new ArrayList<SocketListener>();
    }

    public static IServerStarter getInstance(){
    	return starter;
    }
  
    private void createServer(){
        // Configure Jetty Logging
        if (!(Logging.SHOW_FINER && LOG.isLoggable(Level.FINER))) {
            Log.instance().disableLog();
        }

        // Setup the logger to match the rest of JXTA unless explicitly configured.
        // "LOG_CLASSES" is a Jetty thing.
        if (System.getProperty("LOG_CLASSES") == null) {
            LoggerLogSink logSink = new LoggerLogSink();
            Logger jettyLogger = Logger.getLogger(org.mortbay.http.HttpServer.class.getName());

            logSink.setLogger(jettyLogger);

            try {

                logSink.start();
                Log.instance().add(logSink);

            } catch (Exception ex) {

                Logging.logCheckedSevere(LOG, "Could not configure LoggerLogSink");

            }
        }

        // SPT - these methods call log internally.  If they are called before we add our JUL as a logSink (above)
        //       then a default STDERR logSink will also be added as default: org.mortbay.util.OutputStreamLogSink
        org.mortbay.util.Code.setDebug(Logging.SHOW_FINER && LOG.isLoggable(Level.FINER));
        org.mortbay.util.Code.setSuppressWarnings(!(Logging.SHOW_WARNING && LOG.isLoggable(Level.WARNING)));
        org.mortbay.util.Code.setSuppressStack(!(Logging.SHOW_FINER && LOG.isLoggable(Level.FINER)));

        // Initialize the Jetty HttpServer
        server = new HttpServer();

        // Set up support for downloading midlets.
        if (System.getProperty("net.jxta.http.allowdownload") != null) {
            HttpContext context = server.addContext("/midlets/*");

            context.setResourceBase("./midlets/");
            // context.setDirAllowed(false);
            // context.setServingResources(true);

            // String methods[] = {"GET"};
            ResourceHandler resHandler = new ResourceHandler();

            // resHandler.setAllowedMethods(methods);
            context.addHandler(resHandler);
        }
    }
  
    /**
     * Get the SelectChannelconnector for a given port, or null if none was found
     * @param port
     * @return
     */
    protected SocketListener getConnectorForPort( int port ){
    	if( port <= 0 )
    		throw new IllegalArgumentException( S_ERR_INVALID_PORT);
    	if( this.socketListeners.isEmpty() )
    		return null;
    	for( SocketListener listener: this.socketListeners ){
    		if( listener.getPort() == port )
    			return listener;
    	}
    	return null;
    }

 	/* (non-Javadoc)
	 * @see org.mortbay.jetty.IServerStarter#createServer(java.lang.ClassLoader, java.net.InetAddress, int)
	 */
 	@Override
	public final void addServletHandler( InetAddress useInterface, int port ){
        if( this.server == null )
        	this.createServer();
        else{
        	this.stop();
        }
        if( this.getConnectorForPort(port) != null )
        	return;
        
         // Create the listener and attach it to server.
        InetAddrPort addrPort = new InetAddrPort(useInterface, port);

        listener = new SocketListener(addrPort);

        listener.setMinThreads(MIN_LISTENER_THREADS);
        listener.setMaxThreads(MAX_LISTENER_THREADS);
        listener.setMaxIdleTimeMs((int) MAX_THREAD_IDLE_DURATION);
        this.socketListeners.add(listener);

        server.addListener(listener);
        LOG.log( Level.INFO, "New connector added for port: " + port);
	}
 	
 	@Override
	public void addServlet(Object caller, String callerId, String servletName) {
        // Create a context for the handlers at the root, then add servlet
        // handler for the specified servlet class and add it to the context
        HttpContext handlerContext = server.getContext("/");

        handler = new ServletHandler();

        handler.setUsingCookies(false);
        handler.initialize(handlerContext);

        // Use peer group class loader (useful for HttpMessageServlet)
        handlerContext.setClassLoader( caller.getClass().getClassLoader() );
        handlerContext.addHandler(handler);
        handler.addServlet(MSG_RECEIVER_RELATIVE_URI, servletName ); 		
        handler.getServletContext().setAttribute( callerId, caller );
        handlers.put(caller, handler );
        try {
			this.start();
		} catch (Exception e) {
            Logging.logCheckedSevere(LOG, "Interrupted during start()\n", e);
			e.printStackTrace();
		}        
 	}

 	/**
 	 * Returns the number of servlet handlers registered in this context
 	 * @return
 	 */
 	protected int nrOfServletHandlers(){
        HttpContext handlerContext = server.getContext("/");
 		int index = 0;
        for( HttpHandler handler: handlerContext.getHandlers() ){
     		if( handler instanceof ServletHandler )
     			index++; 			
 		}
        return index;
 	}
 	
 	@Override
    public synchronized void start() throws Exception{
        if( this.started )
        	return;
 		try {
            server.start();
            this.started = true;
            LOG.info("\n\n Started Jetty Server for JXTA");
        } catch (Exception e) {
        	throw( e );
        }
    }

    @Override
    public synchronized void stop( Object caller ) {
            ServletHandler handler = this.handlers.remove( caller ); 
            HttpContext handlerContext = server.getContext("/");
            handlerContext.removeHandler(handler);
            Logging.logCheckedInfo(LOG, "HTTP Servlet Transport removed");
            if( this.nrOfServletHandlers() > 0 )
            	return;
            this.stop();   
     } 	

    synchronized void stop() {
       try {
           if( server == null )
        	   return;
    	   server.stop();
            this.started = false;
            this.server = null;
            LOG.info("\n\n Stopping Jetty Server");
        } catch (InterruptedException e) {
            Logging.logCheckedSevere(LOG, "Interrupted during stop()\n", e);
        }
    } 	
}
