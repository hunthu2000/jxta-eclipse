package net.osgi.jp2p.properties;

import java.net.URISyntaxException;

import net.osgi.jp2p.persistence.PersistedProperty;
import net.osgi.jp2p.properties.IJp2pProperties.Jp2pProperties;

import org.eclipse.core.runtime.preferences.ConfigurationScope;

public abstract class AbstractPreferences {

	private IJp2pWritePropertySource<IJp2pProperties> source;
	
	public AbstractPreferences( IJp2pWritePropertySource<IJp2pProperties> source ) {
		this.source = source;
	}

	public IJp2pWritePropertySource<IJp2pProperties> getSource() {
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
	protected abstract Object convertValue( IJp2pProperties id, String value );
	
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
		boolean persisted = ManagedProperty.isPersisted(property);
		if( persisted ){
			PersistedProperty pp = new PersistedProperty( (String) source.getProperty( Jp2pProperties.BUNDLE_ID ), ConfigurationScope.INSTANCE, this );
			pp.setProperty(id, value);
		}
		Object converted = this.convertValue(id, value);
		if( converted == null )
			return false;
		return property.setValue( converted);
	}
}
