package net.osgi.jp2p.persistence;

import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;

public class DefaultPersistedProperty<T extends Object> extends
		AbstractPersistedProperty<T> {

	public DefaultPersistedProperty() {
		super();
	}

	public DefaultPersistedProperty(IJp2pWritePropertySource<IJp2pProperties> source) {
		super(source);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public T getProperty(IJp2pProperties id) {
		return (T) super.getSource().getProperty(id);
	}

	@Override
	public boolean setProperty(IJp2pProperties id) {
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) super.getSource();
		return source.setProperty(id, null);
	}


}
