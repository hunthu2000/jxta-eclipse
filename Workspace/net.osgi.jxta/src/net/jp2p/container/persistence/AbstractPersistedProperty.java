package net.jp2p.container.persistence;

import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;

public abstract class AbstractPersistedProperty<T,U extends Object> implements
		IPersistedProperties<T,U> {

	private  IJp2pPropertySource<IJp2pProperties> source;

	protected AbstractPersistedProperty() {}

	protected AbstractPersistedProperty( IJp2pWritePropertySource<IJp2pProperties> source ) {
		this.setPropertySource(source);
	}

	/**
	 * set the property source
	 * @param source
	 */
	protected void setPropertySource( IJp2pWritePropertySource<IJp2pProperties> source ){
		this.source = source;
	}

	protected IJp2pPropertySource<IJp2pProperties> getSource() {
		return source;
	}
}
