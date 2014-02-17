package net.jp2p.endpoint.servlethttp.osgi;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxse.module.AbstractModuleFactory;
import net.jxse.module.IJxtaModuleFactory;
import net.jxse.module.JxtaModuleDescriptor;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredDocumentUtils;
import net.jxta.document.XMLDocument;
import net.jxta.document.XMLElement;
import net.jxta.impl.endpoint.servlethttp.ServletHttpTransport;
import net.jxta.impl.peergroup.AutomaticConfigurator;
import net.jxta.impl.protocol.HTTPAdv;
import net.jxta.impl.protocol.PlatformConfig;
import net.jxta.logging.Logging;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.Module;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.TransportAdvertisement;

public class ModuleFactory extends AbstractModuleFactory<Module, ModuleSpecID> implements IJxtaModuleFactory {

	public static final String S_HTTP_SERVICE = "HttpService";

	/**
     * Log4J logger
     */
    private final static transient Logger LOG = Logger.getLogger( ModuleFactory.class.getName());
    
    private boolean reconfigure;    

	public ModuleFactory() {
		super( JxtaModuleDescriptor.parseResource( S_HTTP_SERVICE, ModuleFactory.class )[0]);
		this.reconfigure = false;
	}

	@Override
	public ModuleSpecID getModuleID() {
		return null;
	}

	@Override
	public ModuleClassID getModuleClassID() {
		return PeerGroup.httpProtoClassID;
	}

	@Override
	public ModuleSpecID getModuleSpecID() {
		// TODO Auto-generated method stub
		return null;
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

                            if (Logging.SHOW_CONFIG && LOG.isLoggable(Level.CONFIG)) {
                                LOG.config("Reconfig requested - invalid interface address");
                            }
                        }
                    }
                }
            } catch (RuntimeException advTrouble) {

                if (Logging.SHOW_WARNING && LOG.isLoggable(Level.WARNING)) {
                    LOG.log(Level.WARNING, "HTTP advertisement corrupted", advTrouble);
                }

                httpAdv = null;
            }
        }
        if (httpAdv == null) {
            if (Logging.SHOW_CONFIG && LOG.isLoggable(Level.CONFIG)) {
                LOG.config("HTTP advertisement missing, making a new one.");
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
                        if (Logging.SHOW_WARNING && LOG.isLoggable(Level.WARNING)) {
                            LOG.warning("Property \'jxta.http.port\' is not a valid port number : " + propertyPort);
                        }
                    }
                } catch (NumberFormatException ignored) {
                    if (Logging.SHOW_WARNING && LOG.isLoggable(Level.WARNING)) {
                        LOG.warning("Property \'jxta.http.port\' was not an integer : " + http);
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
}
