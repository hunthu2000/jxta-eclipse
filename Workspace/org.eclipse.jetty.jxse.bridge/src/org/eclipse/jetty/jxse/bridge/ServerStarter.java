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
package org.eclipse.jetty.jxse.bridge;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxse.osgi.jetty.service.IServerStarter;
import net.jxta.logging.Logging;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class ServerStarter implements IServerStarter {
	
	private static final String S_ERR_INVALID_PORT = "The required port is invalid. Should be larger than zero";

    /**
     * the relative URI of where the message receiver servlet will be mounted.
     */
    private final static String MSG_RECEIVER_RELATIVE_URI = "/*";

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
    private Server server;
    private SelectChannelConnector connector;
    private boolean started;
  
    private Map<Object, ServletContextHandler> handlers;

    /**
     *  logger
     */
    private final static transient Logger LOG = Logger.getLogger(ServerStarter.class.getName());

    private static IServerStarter starter = new ServerStarter();
    
    public ServerStarter(){
     	this.started = false;
    	handlers = new HashMap<Object, ServletContextHandler>();
   }

    public static IServerStarter getInstance(){
    	return starter;
    }
  
    private void createServer(){
        
    	Properties p = new Properties();
    	p.setProperty("org.eclipse.jetty.LEVEL", "WARN");
    	org.eclipse.jetty.util.log.StdErrLog.setProperties(p);
    	
        // Initialize the Jetty HttpServer
        server = new Server();
        HandlerCollection handler = new HandlerCollection();
        // Set up support for downloading midlets.
        if (System.getProperty("net.jxta.http.allowdownload") != null) {
            ContextHandler context = new ContextHandler();
            context.setContextPath( "/midlets/*");
            context.setResourceBase("./midlets/");
            //context.setDirAllowed(false);
            // context.setServingResources(true);

            // String methods[] = {"GET"};
            ResourceHandler resHandler = new ResourceHandler();
            // resHandler.setAllowedMethods(methods);
            context.setHandler(resHandler);
            handler.addHandler( context );           
        }
        server.setHandler( handler );
    }
   
    /**
     * Get the SelectChannelconnector for a given port, or null if none was found
     * @param port
     * @return
     */
    protected SelectChannelConnector getConnectorForPort( int port ){
    	if( port <= 0 )
    		throw new IllegalArgumentException( S_ERR_INVALID_PORT);
    	if(( server.getConnectors() == null ) ||( server.getConnectors().length == 0 ))
    		return null;
    	for( Connector connector: server.getConnectors() ){
    		if(!( connector instanceof SelectChannelConnector ))
    			continue;
    		int connectorPort = connector.getLocalPort();
    		if( connectorPort == port )
    			return (SelectChannelConnector) connector;
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

        //Do not add a new connector if one is already listening to the given port.
 		if( this.getConnectorForPort(port) != null )
        	return;
 		connector = new SelectChannelConnector();
        connector.setPort(port);
        connector.setThreadPool(new QueuedThreadPool(MAX_LISTENER_THREADS));
        connector.setMaxIdleTime((int) MAX_THREAD_IDLE_DURATION);
        server.addConnector(connector);
        LOG.log( Level.INFO, "New connector added for port: " + port);
	}
 	
 	/* (non-Javadoc)
	 * @see org.mortbay.jetty.IServerStarter#addServlet(java.lang.String)
	 */
 	@Override
	public void addServlet( Object caller, String callerId, String servletName ){

        // Create a context for the handlers at the root, then add servlet
        // handler for the specified servlet class and add it to the context
  		ServletContextHandler handlerContext = new ServletContextHandler();
        handlerContext.setContextPath("/");
        handlerContext.setClassLoader( caller.getClass().getClassLoader() );
        handlerContext.addServlet( servletName, MSG_RECEIVER_RELATIVE_URI);
        handlerContext.getServletContext().setAttribute( callerId, caller);
        HandlerCollection col = ( HandlerCollection )server.getHandler();
        col.addHandler(handlerContext);
        handlers.put(caller, handlerContext );
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
 	protected int nrOfServletContextHandlers(){
 		int index = 0;
        HandlerCollection collection = ( HandlerCollection )server.getHandler(); 
        for( Handler handler: collection.getHandlers() ){
     		if( handler instanceof ServletContextHandler )
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
    	ServletContextHandler handler = this.handlers.remove( caller ); 
    	HandlerCollection collection = ( HandlerCollection )server.getHandler(); 
    	collection.removeHandler( handler);
       	Logging.logCheckedInfo(LOG, "HTTP Servlet Transport removed.");
       	if( this.nrOfServletContextHandlers() > 0 )
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
            LOG.info("\n\n Started Jetty Server");
        } catch ( Exception e) {
            Logging.logCheckedSevere(LOG, "Interrupted during stop()\n", e);
        }
    } 	
}
