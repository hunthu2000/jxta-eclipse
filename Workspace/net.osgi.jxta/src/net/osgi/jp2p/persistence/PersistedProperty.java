package net.osgi.jp2p.persistence;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import net.osgi.jp2p.properties.AbstractPreferences;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.utils.Utils;

public class PersistedProperty<E extends Object> {

	private AbstractPreferences<E> prefs;
	private Preferences preferences;
	
	public PersistedProperty( IScopeContext scope, AbstractPreferences<E> prefs ) {
		this.prefs = prefs;
		IJp2pPropertySource<E> source = prefs.getSource();
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
		String value = this.preferences.get( id.toString(), mp.getDefaultValue().toString());
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
		this.preferences.put( id.toString(), value.toString());
		try {
			this.preferences.flush();
			return true;
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
