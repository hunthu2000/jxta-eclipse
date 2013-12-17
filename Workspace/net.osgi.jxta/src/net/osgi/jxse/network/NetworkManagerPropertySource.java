package net.osgi.jxse.network;

import java.util.Iterator;

import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.utils.ProjectFolderUtils;
import net.osgi.jxse.utils.StringStyler;

public class NetworkManagerPropertySource extends AbstractJxseWritePropertySource<IJxseProperties>
	implements IJxseWritePropertySource<IJxseProperties>
{

	public enum NetworkManagerProperties implements IJxseProperties{
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
	
	public NetworkManagerPropertySource( JxseContextPropertySource source) {
		super( source.getComponentName(), source );
		this.fill( source );
	}

	@Override
	public String getComponentName() {
		return Components.NETWORK_MANAGER.toString();
	}

	private void fill( JxseContextPropertySource source ){
		Iterator<IJxseProperties> iterator = source.propertyIterator();
		this.setDirective( Directives.AUTO_START, source.getDirective( Directives.AUTO_START ));
		this.setDirective( Directives.CLEAR_CONFIG, source.getDirective( Directives.CLEAR_CONFIG ));
		while( iterator.hasNext() ){
			ContextProperties cp = (ContextProperties) iterator.next();
			IJxseProperties nmp = convertFrom( cp );
			if( nmp == null )
				continue;
			Object retval = source.getProperty( cp );
			if( NetworkManagerProperties.INSTANCE_HOME.equals(nmp ) && ( retval instanceof String ))
				retval = ProjectFolderUtils.getParsedUserDir((String) retval, super.getBundleId());
			super.setProperty(nmp, retval, true);
		}	
	}

	@Override
	public NetworkManagerProperties getIdFromString(String key) {
		return NetworkManagerProperties.valueOf( key );
	}

	@Override
	public Object getDefault( IJxseProperties id) {
		JxseContextPropertySource source = (JxseContextPropertySource) super.getParent();
		return source.getDefault( convertTo( id ));
	}

	/**
	 * Convenience method to transport the TCP port, although it is not strictly a property of the
	 * network manager 
	 * @return
	 */
	public int getTcpPort(){
		JxseContextPropertySource source = (JxseContextPropertySource) super.getParent();
		Object port = source.getProperty( ContextProperties.PORT );
		if( port == null )
			return 0;
		return ( Integer )port;
	}
	
	@Override
	public boolean validate( IJxseProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * convert the given context property to a networkManagerProperty, or null if there is
	 * no relation between them
	 * @param context
	 * @return
	 */
	public IJxseProperties convertFrom( ContextProperties context ){
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
	public ContextProperties convertTo( IJxseProperties id ){
		if(!( id instanceof IJxseProperties ))
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
