package net.jp2p.container.persistence;

import java.net.URISyntaxException;

import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IPropertyConvertor;
import net.jp2p.container.properties.ManagedProperty;

public abstract class AbstractPreferences<T,U extends Object> implements IPropertyConvertor<T,U> {

	private IJp2pPropertySource<IJp2pProperties> source;

	public AbstractPreferences() {
	}

	public AbstractPreferences( IJp2pWritePropertySource<IJp2pProperties> source ) {
		this.source = source;
	}

	protected IJp2pPropertySource<IJp2pProperties> getSource() {
		return source;
	}
	
	/**
	 * Create a default value for the given id
	 * @param id
	 * @return
	 */
	public Object createDefaultValue( IJp2pProperties id ){
		boolean create = ManagedProperty.isCreated( source.getManagedProperty(id));
		if( !create )
			return null;
		ManagedProperty<IJp2pProperties, Object> property = source.getManagedProperty(id);
		if( property != null )
			return property.getDefaultValue();
		return null;
	}
	
	/**
	 * Set the correct property from the given string
	 * @param property
	 * @param value
	 * @return
	 * @throws URISyntaxException
	 */
	public boolean setPropertyFromConverion( IJp2pProperties id, T value ){
		ManagedProperty<IJp2pProperties,Object> property = this.source.getManagedProperty(id);
		Object converted = this.convertTo( id, value);
		if( converted == null )
			return false;
		return property.setValue( converted);
	}
}
