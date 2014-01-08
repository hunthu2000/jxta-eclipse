package net.osgi.jp2p.jxta.network;

import java.util.Iterator;

import net.osgi.jp2p.container.Jp2pContainerPropertySource;
import net.osgi.jp2p.container.IJxseServiceContainer.ContextProperties;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.utils.ProjectFolderUtils;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;
import net.osgi.jp2p.properties.AbstractJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;

public class NetworkManagerPropertySource extends AbstractJp2pWritePropertySource
	implements IJp2pWritePropertySource<IJp2pProperties>
{
	/**
	 * Supported properties
	 * @author Kees
	 *
	 */
	public enum NetworkManagerProperties implements IJp2pProperties{
		CONFIG_PERSISTENT,
		INFRASTRUCTURE_ID,
		INSTANCE_HOME,
		INSTANCE_NAME,
		MODE,
		PEER_ID;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	/**
	 * supported directives
	 * @author Kees
	 *
	 */
	public enum NetworkManagerDirectives implements IJp2pDirectives{
		CLEAR_CONFIG;

		public static boolean isValidDirective( String str ){
			if( Utils.isNull( str ))
				return false;
			for( NetworkManagerDirectives dir: values() ){
				if( dir.name().equals( str ))
					return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public NetworkManagerPropertySource( Jp2pContainerPropertySource parent) {
		super( parent.getComponentName(), parent );
		this.fill( parent );
	}

	@Override
	public String getComponentName() {
		return JxtaComponents.NETWORK_MANAGER.toString();
	}

	private void fill( Jp2pContainerPropertySource parent ){
		Iterator<IJp2pProperties> iterator = parent.propertyIterator();
		this.setDirective( Directives.AUTO_START, parent.getDirective( Directives.AUTO_START ));
		this.setDirective( Directives.CLEAR, parent.getDirective( Directives.CLEAR ));
		while( iterator.hasNext() ){
			ContextProperties cp = (ContextProperties) iterator.next();
			IJp2pProperties nmp = convertFrom( cp );
			if( nmp == null )
				continue;
			Object retval = parent.getProperty( cp );
			if( NetworkManagerProperties.INSTANCE_HOME.equals(nmp ) && ( retval instanceof String ))
				retval = ProjectFolderUtils.getParsedUserDir((String) retval, super.getBundleId());
			super.setProperty(nmp, retval, true);
		}
		String name=  parent.getDirective( Directives.NAME );
		super.setProperty(NetworkManagerProperties.INSTANCE_NAME, name);
	}

	
	@Override
	public boolean setDirective(IJp2pDirectives id, String value) {
		if( NetworkManagerDirectives.isValidDirective( id.name() ))
			return super.setDirective( NetworkManagerDirectives.valueOf( id.name() ), value );
		return super.setDirective(id, value);
	}

	@Override
	public NetworkManagerProperties getIdFromString(String key) {
		return NetworkManagerProperties.valueOf( key );
	}

	@Override
	public Object getDefault( IJp2pProperties id) {
		Jp2pContainerPropertySource source = (Jp2pContainerPropertySource) super.getParent();
		return source.getDefault( convertTo( id ));
	}

	/**
	 * Convenience method to transport the TCP port, although it is not strictly a property of the
	 * network manager 
	 * @return
	 */
	public int getTcpPort(){
		Jp2pContainerPropertySource source = (Jp2pContainerPropertySource) super.getParent();
		Object port = source.getProperty( ContextProperties.PORT );
		if( port == null )
			return 0;
		return ( Integer )port;
	}
	
	@Override
	public boolean validate( IJp2pProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * convert the given context property to a networkManagerProperty, or null if there is
	 * no relation between them
	 * @param context
	 * @return
	 */
	public IJp2pProperties convertFrom( ContextProperties context ){
		switch( context ){
		case CONFIG_MODE:
			return NetworkManagerProperties.MODE;
		case HOME_FOLDER:
			return NetworkManagerProperties.INSTANCE_HOME;
		case PEER_ID:
			return NetworkManagerProperties.PEER_ID;
		default:
			break;
		}
		return null;
	}

	/**
	 * convert the given context property to a networkManagerProperty, or null if there is
	 * no relation between them
	 * @param context
	 * @return
	 */
	public ContextProperties convertTo( IJp2pProperties id ){
		if(!( id instanceof IJp2pProperties ))
			return null;
		NetworkManagerProperties props = (NetworkManagerProperties) id;
		switch( props ){
		case MODE:
			return ContextProperties.CONFIG_MODE;
		case INSTANCE_HOME:
			return ContextProperties.HOME_FOLDER;
		case PEER_ID:
			return ContextProperties.PEER_ID;
		default:
			break;
		}
		return null;
	}
}
