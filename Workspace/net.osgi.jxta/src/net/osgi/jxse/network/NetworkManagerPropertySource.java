package net.osgi.jxse.network;

import java.util.Iterator;

import net.osgi.jxse.context.IJxseServiceContext.ContextDirectives;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.AbstractJxsePropertySource;
import net.osgi.jxse.utils.ProjectFolderUtils;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class NetworkManagerPropertySource extends AbstractJxsePropertySource<NetworkManagerPropertySource.NetworkManagerProperties, ContextDirectives>{

	public enum NetworkManagerProperties{
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

	private JxseContextPropertySource source;
	
	public NetworkManagerPropertySource( JxseContextPropertySource source) {
		super( source.getBundleId(), source.getIdentifier(), Components.NETWORK_MANAGER.name() );
		this.fill( source );
		this.source = source;
	}

	private void fill( JxseContextPropertySource source ){
		Iterator<ContextProperties> iterator = source.propertyIterator();
		while( iterator.hasNext() ){
			ContextProperties cp = iterator.next();
			NetworkManagerProperties nmp = convertFrom( cp );
			if( nmp == null )
				continue;
			Object retval =  source.getProperty( cp );
			if( NetworkManagerProperties.INSTANCE_HOME.equals( nmp ))
				retval = ProjectFolderUtils.getParsedUserDir((String) retval, super.getBundleId());
			super.setProperty(nmp, retval);
		}	
	}

	@Override
	public Object getDefault(NetworkManagerProperties id) {
		return source.getDefault( convertTo( id ));
	}

	@Override
	public boolean validate(NetworkManagerProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getDefaultDirectives(ContextDirectives id) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * convert the given context property to a networkManagerProperty, or null if there is
	 * no relation between them
	 * @param context
	 * @return
	 */
	public NetworkManagerProperties convertFrom( ContextProperties context ){
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
	public ContextProperties convertTo( NetworkManagerProperties props ){
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
