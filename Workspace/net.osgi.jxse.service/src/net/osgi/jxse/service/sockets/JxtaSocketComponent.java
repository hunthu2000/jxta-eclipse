package net.osgi.jxse.service.sockets;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaSocket;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.JxseComponent;
import net.osgi.jxse.socket.SocketFactory;
import net.osgi.jxse.utils.IOUtils;

public class JxtaSocketComponent extends JxseComponent<JxtaSocket> {

	public static final String S_SERVER_SOCKET = "JXTA ServerSocket";

	private IJxseComponent<PipeAdvertisement> pipeAd;

	
	public JxtaSocketComponent( IJxseComponent<NetworkManager> manager, IJxseComponent<PipeAdvertisement> pipeAd, 
			Properties properties ) {
		super( manager, properties );
		this.pipeAd = pipeAd;
	}

	@Override
	public boolean isRoot() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JxtaSocket getModule() {
		JxtaSocket socket = null;
		 IJxseComponent<NetworkManager> manager = (net.osgi.jxse.component.IJxseComponent<NetworkManager> )super.getParent();
		try {
			return new JxtaSocket( manager.getModule().getNetPeerGroup(), null, pipeAd.getModule(), ( int )super.getProperty( SocketFactory.Properties.TIME_OUT ));
		} catch (Exception e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
			IOUtils.closeSocket( socket );
		}
		return null;
	}
}
