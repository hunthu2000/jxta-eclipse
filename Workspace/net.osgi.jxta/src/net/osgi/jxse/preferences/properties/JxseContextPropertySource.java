package net.osgi.jxse.preferences.properties;

import java.io.File;
import java.net.URISyntaxException;

import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.context.IJxseServiceContext.ContextDirectives;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.preferences.IJxsePreferences;
import net.osgi.jxse.preferences.ProjectFolderUtils;
import net.osgi.jxse.utils.Utils;

public class JxseContextPropertySource extends AbstractJxsePropertySource<ContextProperties, ContextDirectives>{

	public static final String JXSE_CONTEXT = "JxseContext";
	public static final String DEF_HOME_FOLDER = "${user.home}/.jxta/${plugin-id}";

	public static final int DEF_MIN_PORT = 1000;
	public static final int DEF_MAX_PORT = 9999;
	public static final int DEF_PORT = 9715;
	
	private String plugin_id, identifier;
	
	public JxseContextPropertySource( String plugin_id, String identifier) {
		this.plugin_id = plugin_id;
		this.identifier = identifier;
	}

	@Override
	public String getComponentName() {
		return JXSE_CONTEXT;
	}

	@Override
	public Object getDefault(ContextProperties id) {
		String str = null;
		switch( id ){
		case HOME_FOLDER:
			str = ProjectFolderUtils.getDefaultJxseDir( IJxsePreferences.S_JXTA, plugin_id ).getPath();
			File file = new File( str );
			return file.toURI();
		case CONFIG_MODE:
			return ConfigMode.valueOf( NetworkManager.ConfigMode.EDGE.name() );
		case RENDEZVOUZ_AUTOSTART:
			return true;
		case PEER_ID:
			str = ( String )this.getProperty( ContextProperties.IDENTIFIER);
			if( Utils.isNull(str))
				return null;
			PeerID pgId = IDFactory.newPeerID( PeerGroupID.defaultNetPeerGroupID, ( str.getBytes() ));
			try {
				return (PeerID) IDFactory.fromURI( pgId.toURI() );
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			break;
		case IDENTIFIER:
			return identifier;
		case PLUGIN_ID:
			return plugin_id;
		case PORT:
			return DEF_PORT;
		}
		return null;
	}

	@Override
	public Object getDefaultDirectives(ContextDirectives id) {
		switch( id ){
		case AUTO_START:
			return true;
		case PEER_ID_CREATE:
			return true;
		case PEER_ID_PERSIST:
			return true;
		}
		return null;
	}

	@Override
	public boolean validate(ContextProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}
