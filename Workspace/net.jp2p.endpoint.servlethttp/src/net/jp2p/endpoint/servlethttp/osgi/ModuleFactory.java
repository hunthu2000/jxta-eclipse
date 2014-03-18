package net.jp2p.endpoint.servlethttp.osgi;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jp2p.container.utils.IOUtils;
import net.jp2p.container.utils.Utils;
import net.jp2p.endpoint.servlethttp.factory.HttpServiceFactory;
import net.jxse.module.AbstractModuleFactory;
import net.jxse.platform.IJxtaModuleFactory;
import net.jxse.platform.JxtaModuleDescriptor;
import net.jxta.compatibility.impl.peergroup.AutomaticConfigurator;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredDocumentUtils;
import net.jxta.document.XMLDocument;
import net.jxta.document.XMLElement;
import net.jxta.impl.endpoint.servlethttp.ServletHttpTransport;
import net.jxta.impl.peergroup.CompatibilityUtils;
import net.jxta.impl.protocol.HTTPAdv;
import net.jxta.impl.protocol.PlatformConfig;
import net.jxta.logging.Logging;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.Module;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.TransportAdvertisement;

public class ModuleFactory extends AbstractModuleFactory<Module, ModuleSpecID> implements IJxtaModuleFactory {

	public static final String S_UTF_8 = "UTF-8";
	public static final String S_HASH = "#";

	public static final String S_RESOURCE_LOCATION = "/Services/net.jxta.platform.Module";

    private final static transient Logger logger = Logger.getLogger( ModuleFactory.class.getName());
    
    private boolean reconfigure;    
 
    private ModuleImplAdvertisement implAdv;
    
    public ModuleFactory( String identifier ) {
		super( JxtaModuleDescriptor.parseResource( identifier, ModuleFactory.class )[0]);
		this.reconfigure = false;
		URL url = ModuleFactory.class.getResource( S_RESOURCE_LOCATION );
		Collection<ModuleImplAdvertisement> advs = locateModuleImplementations( url );
		implAdv = advs.iterator().next();
	}

	@Override
	public ModuleSpecID getModuleID() {
		return implAdv.getModuleSpecID();
	}

	@Override
	public ModuleClassID getModuleClassID() {
		return PeerGroup.httpProtoClassID;
	}

	@Override
	public ModuleSpecID getModuleSpecID() {
		return implAdv.getModuleSpecID();
	}

	@Override
	public ModuleImplAdvertisement getModuleImplAdvertisement() {
		return implAdv;
	}

	@Override
	public boolean isReconfigure() {
		return this.reconfigure;
	}

	/**
	 * Get the XML document that describes the module
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Advertisement getAdvertisement( PlatformConfig config ){
        XMLDocument http = (XMLDocument) config.getServiceParam( getModuleClassID());
        HTTPAdv httpAdv = null;
        boolean httpEnabled = true;

        if (http != null) {
            try {
                httpEnabled = config.isSvcEnabled(getModuleClassID());

                XMLElement<?> param = null;

                Enumeration<?> httpChilds = http.getChildren(TransportAdvertisement.getAdvertisementType());

                // get the HTTPAdv from TransportAdv
                if (httpChilds.hasMoreElements()) {
                    param = (XMLElement<?>) httpChilds.nextElement();
                }

                if (null != param) {
                    httpAdv = (HTTPAdv) AdvertisementFactory.newAdvertisement(param);

                    if (httpEnabled) {
                        // check if the interface address is still valid.
                        String intf = httpAdv.getInterfaceAddress();

                        if ((null != intf) && !AutomaticConfigurator.isValidInetAddress(intf)) {
                            this.reconfigure = true;

                            if (Logging.SHOW_CONFIG && logger.isLoggable(Level.CONFIG)) {
                                logger.config("Reconfig requested - invalid interface address");
                            }
                        }
                    }
                }
            } catch (RuntimeException advTrouble) {

                if (Logging.SHOW_WARNING && logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING, "HTTP advertisement corrupted", advTrouble);
                }

                httpAdv = null;
            }
        }
        if (httpAdv == null) {
            if (Logging.SHOW_CONFIG && logger.isLoggable(Level.CONFIG)) {
                logger.config("HTTP advertisement missing, making a new one.");
            }

            int port = 0;
            // get the port from a property
            String httpPort = System.getProperty("jxta.http.port");

            if (httpPort != null) {
                try {
                    int propertyPort = Integer.parseInt(httpPort);

                    if ((propertyPort < 65536) && (propertyPort >= 0)) {
                        port = propertyPort;
                    } else {
                        if (Logging.SHOW_WARNING && logger.isLoggable(Level.WARNING)) {
                            logger.warning("Property \'jxta.http.port\' is not a valid port number : " + propertyPort);
                        }
                    }
                } catch (NumberFormatException ignored) {
                    if (Logging.SHOW_WARNING && logger.isLoggable(Level.WARNING)) {
                        logger.warning("Property \'jxta.http.port\' was not an integer : " + http);
                    }
                }
            }

            httpAdv = (HTTPAdv) AdvertisementFactory.newAdvertisement(HTTPAdv.getAdvertisementType());
            httpAdv.setProtocol("http");
            httpAdv.setPort(port);
            httpAdv.setServerEnabled(false);

            // Create new param docs that contain the updated adv
            http = (XMLDocument<?>) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, "Parm");
            XMLDocument<?> httAdvDoc = (XMLDocument<?>) httpAdv.getDocument(MimeMediaType.XMLUTF8);

            StructuredDocumentUtils.copyElements(http, http, httAdvDoc);
            if (!httpEnabled) {
                http.appendChild(http.createElement("isOff"));
            }
            config.putServiceParam( getModuleClassID(), http);
        
        }
		return httpAdv;
	}

	@Override
	public Module onCreateModule() {
		return new ServletHttpTransport();
	}

    /**
     * Register instance classes given a URL to a file containing modules which
     * must be found on the current class path. Each line of the file contains a 
     * module spec ID, the class name and the Module description. The fields are 
     * separated by whitespace. Comments are marked with a '#', the pound sign. 
     * Any text following # on any line in the file is ignored.
     *
     * @param specID ModuleSpecID that we are seeking implementations of
     * @param providers URL to a resource containing a list of providers.
     * @return list of discovered ModuleImplAdvertisements for the specified
     *  ModuleSpecID, or null if no results were found.
     */
    protected static Collection<ModuleImplAdvertisement> locateModuleImplementations( URL providers) {

        Logger logger = Logger.getLogger( HttpServiceFactory.class.getName());

        Collection<ModuleImplAdvertisement> result = new ArrayList<ModuleImplAdvertisement>();
        InputStream urlStream = null;
        try {
            urlStream = providers.openStream();
   		}
		catch( Exception ex ){
			ex.printStackTrace();
			IOUtils.closeInputStream( urlStream);
			return null;
		}
        String provider;
        Scanner scanner = new Scanner( urlStream );
        try{
        	while( scanner.hasNextLine() ){
        		provider = scanner.nextLine();
        		if( Utils.isNull( provider ))
        			continue;

        		int comment = provider.indexOf( S_HASH );
        		if (comment != -1) {
        			provider = provider.substring(0, comment);
        		}
        		provider = provider.trim();
        		if (0 == provider.length())
        			continue;
        		try {
        			ModuleImplAdvertisement mAdv = null;
        			String[] parts = provider.split("\\s", 3);
        			if (parts.length == 1) {
        				// Standard Jar SPI format:  Class name
        				mAdv = locateModuleImplAdvertisement( parts[0]);
        			} else if (parts.length == 3) {
        				// Current non-standard format: MSID, Class name, Description
        				ModuleSpecID msid = ModuleSpecID.create(URI.create(parts[0]));
        				String code = parts[1];
        				String description = parts[2];
        				mAdv = locateModuleImplAdvertisement( code);
        				if (mAdv == null) {
        					mAdv = CompatibilityUtils.createModuleImplAdvertisement(msid, code, description);
        				}
        			} else {
        				logger.severe( "Failed to register: " + provider );
        			}
        			if (mAdv != null) {
        				result.add(mAdv);
        			}
        		} catch (Exception allElse) {
        			logger.severe( "Failed to register:" + provider );
        		}
        	}
        }
        catch( Exception ex ){
        	ex.printStackTrace();
        }
        finally{
        	scanner.close();
        	IOUtils.closeInputStream( urlStream);
        }
        return result;
    }

    /**
     * Attempts to locate the ModuleImplAdvertisement of a module by
     * the use of reflection.
     * 
     * @param className class name to examine
     * @return ModuleImplAdvertisement found by introspection, or null if
     *  the ModuleImplAdvertisement could not be discovered in this manner
     */
    private static ModuleImplAdvertisement locateModuleImplAdvertisement( String className) {
        Logger logger = Logger.getLogger( HttpServiceFactory.class.getName());
        try {
            Class<?> moduleClass = (Class<?>) Class.forName(className);
            Class<? extends Module> modClass = verifyAndCast(moduleClass);
            Method getImplAdvMethod = modClass.getMethod("getDefaultModuleImplAdvertisement");
            return (ModuleImplAdvertisement) getImplAdvMethod.invoke(null);
        } catch(Exception ex) {
            logger.severe( ": Could not introspect Module for MIA: " + className );
        }
        return null;
    }

    /**
     * Checks that a class is a Module.  If not, it raises a an exception.
     * If it is, it casts the generic class to the subtype.
     * 
     * @param clazz generic class to verify
     * @return Module subtype class
     * @throws ClassNotFoundException if class was not of the proper type
     */
    private static Class<? extends Module> verifyAndCast(Class<?> clazz)
    throws ClassNotFoundException {
        try {
            return clazz.asSubclass(Module.class);
        } catch (ClassCastException ccx) {
            throw(new ClassNotFoundException(
                    "Class found but was not a Module class: " + clazz));
        }
    }
}
