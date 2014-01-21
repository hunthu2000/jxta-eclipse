package net.osgi.jp2p.properties;

import java.net.URISyntaxException;

import net.osgi.jp2p.persistence.IPersistedProperty;

public abstract class AbstractPreferences<T extends Object> implements IPersistedProperty<T> {

	private IJp2pWritePropertySource<IJp2pProperties> source;

	public AbstractPreferences() {
	}

	public AbstractPreferences( IJp2pWritePropertySource<IJp2pProperties> source ) {
		this.source = source;
	}

	protected IJp2pWritePropertySource<IJp2pProperties> getSource() {
		return source;
	}

	
	@Override
	public void setPropertySource(IJp2pWritePropertySource<IJp2pProperties> source) {
		this.source = source;
	}

	@Override
	public T getProperty(IJp2pProperties id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setProperty(IJp2pProperties id) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Perform the conversion of a String value to something else
	 * @param id
	 * @param value
	 * @return
	 */
	protected abstract T convertValue( IJp2pProperties id, String value );
	
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
	public boolean setPropertyFromString( IJp2pProperties id, String value ){
		ManagedProperty<IJp2pProperties,Object> property = this.source.getManagedProperty(id);
		Object converted = this.convertValue(id, value);
		if( converted == null )
			return false;
		return property.setValue( converted);
	}
}
