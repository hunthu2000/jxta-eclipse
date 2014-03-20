package net.jp2p.container.properties;

import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;

public abstract class AbstractPropertyConvertor<T,U extends Object> implements
		IPropertyConvertor<T,U> {

	private  IJp2pWritePropertySource<IJp2pProperties> source;

	protected AbstractPropertyConvertor() {}

	protected AbstractPropertyConvertor( IJp2pWritePropertySource<IJp2pProperties> source ) {
		this.setPropertySource(source);
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

	public boolean setPropertyFromConverion(IJp2pProperties id, T value) {
		source.getOrCreateManagedProperty(id, this.convertTo(id, value), false);
		return false;
	}

}
