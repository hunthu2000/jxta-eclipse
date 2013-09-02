package net.osgi.jxse.service.network;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.service.core.AbstractJxtaService;
import net.osgi.jxse.service.peergroup.IPeerGroupProperties.PeerGroupProperties;

public class NetPeerGroupService extends AbstractJxtaService<PeerGroup>{

	public static final String S_NETWORK_MANAGER = "Jxta Network Manager";

	private NetworkManager manager;

	public NetPeerGroupService( NetworkManager manager ) {
		this.manager = manager;
	}

	public Object getProperty( PeerGroupProperties key ){
		return super.getProperty(key);
	}
	
	public NetworkManager getNetworkManager(){
		return manager;
	}

	@Override
	public Iterator<?> iterator() {
		List<PeerGroupProperties> set = Arrays.asList(PeerGroupProperties.values());
		return set.iterator();
	}

	
	@Override
	protected void activate() {
		try {
			PeerGroup peergroup = manager.startNetwork();
			super.setModule( peergroup );
		} catch (PeerGroupException | IOException e) {
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( Level.SEVERE, e.getMessage() );
			e.printStackTrace();
		}
		super.activate();
	}

	@Override
	protected void deactivate() {
		manager.stopNetwork();
	}

	@Override
	protected void fillDefaultValues() {
		// TODO Auto-generated method stub
		
	}
}