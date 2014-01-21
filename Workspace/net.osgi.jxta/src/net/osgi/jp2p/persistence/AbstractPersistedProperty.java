package net.osgi.jp2p.persistence;

import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public abstract class AbstractPersistedProperty<T extends Object> implements
		IPersistedProperty<T> {

	private  IJp2pPropertySource<IJp2pProperties> source;

	protected AbstractPersistedProperty() {}

	protected AbstractPersistedProperty( IJp2pWritePropertySource<IJp2pProperties> source ) {
		this.source = source;
	}

	/**
	 * set the property source
	 * @param source
	 */
	public void setPropertySource( IJp2pWritePropertySource<IJp2pProperties> source ){
		this.source = source;
	}

	protected IJp2pPropertySource<IJp2pProperties> getSource() {
		return source;
	}

	@Override
	public boolean setProperty(IJp2pProperties id) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
