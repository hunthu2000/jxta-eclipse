package net.osgi.jxse.properties;

import java.net.URISyntaxException;

import net.osgi.jxse.persistence.PersistedProperty;

import org.eclipse.core.runtime.preferences.ConfigurationScope;

public abstract class AbstractPreferences<E extends Object> {

	private IJxseWritePropertySource<E> source;
	
	public AbstractPreferences( IJxseWritePropertySource<E> source ) {
		this.source = source;
	}

	public IJxseWritePropertySource<E> getSource() {
		return source;
	}

	/**
	 * Get the name of the preference store
	 * @return
	 */
	public String getName()
	{
		return source.getComponentName();
	}

	/**
	 * Perform the conversion of a String value to something else
	 * @param id
	 * @param value
	 * @return
	 */
	protected abstract Object convertValue( E id, String value );
	
	/**
	 * Create a default value for the given id
	 * @param id
	 * @return
	 */
	public Object createDefaultValue( E id ){
		boolean create = ManagedProperty.isCreated( source.getManagedProperty(id));
		if( !create )
			return null;
		ManagedProperty<E, Object> property = source.getManagedProperty(id);
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean setPropertyFromString( E id, String value ){
		ManagedProperty<E,Object> property = this.source.getManagedProperty(id);
		boolean persisted = ManagedProperty.isPersisted(property);
		if( persisted ){
			PersistedProperty<E> pp = new PersistedProperty( ConfigurationScope.INSTANCE, this );
			pp.setProperty(id, value);
		}
		Object converted = this.convertValue(id, value);
		if( converted == null )
			return false;
		return property.setValue( converted);
	}
}
