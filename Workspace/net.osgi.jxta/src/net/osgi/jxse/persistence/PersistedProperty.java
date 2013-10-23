package net.osgi.jxse.persistence;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import net.osgi.jxse.properties.AbstractPreferences;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.utils.Utils;

public class PersistedProperty<E extends Enum<E>> {

	private AbstractPreferences<E, IJxseDirectives> prefs;
	private Preferences preferences;
	
	public PersistedProperty( IScopeContext scope, AbstractPreferences<E, IJxseDirectives> prefs ) {
		this.prefs = prefs;
		IJxsePropertySource<E,IJxseDirectives> source = prefs.getSource();
		Preferences pref1 = scope.getNode( source.getBundleId() );
		Preferences pref2 = pref1.node( source.getIdentifier() );
		this.preferences = pref2.node( source.getComponentName() );
	}

	/**
	 * Get the persisted property with the given id, if it exists
	 * @param id
	 * @return
	 */
	public Object getProperty( E id ){
		ManagedProperty<E, Object> mp = this.prefs.getSource().getManagedProperty(id);
		String value = this.preferences.get( id.name(), mp.getDefaultValue().toString());
		if( !Utils.isNull( value )){
			this.prefs.setPropertyFromString(id, value);
		}
		return id;
	}
	
	/**
	 * Set the property of a preference store
	 * @param id
	 * @param value
	 * @return
	 */
	public boolean setProperty( E id, Object value ){
		this.preferences.put( id.name(), value.toString());
		try {
			this.preferences.flush();
			return true;
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
