package net.osgi.jp2p.persistence;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import net.osgi.jp2p.properties.AbstractPreferences;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.utils.Utils;

public class PersistedProperty {

	private AbstractPreferences prefs;
	private Preferences preferences;
	
	public PersistedProperty( String bundle_id, IScopeContext scope, AbstractPreferences prefs ) {
		this.prefs = prefs;
		IJp2pPropertySource<IJp2pProperties> source = prefs.getSource();
		Preferences pref1 = scope.getNode( bundle_id );
		Preferences pref2 = pref1.node( source.getId() );
		this.preferences = pref2.node( source.getComponentName() );
	}

	/**
	 * Get the persisted property with the given id, if it exists
	 * @param id
	 * @return
	 */
	public Object getProperty( IJp2pProperties id ){
		ManagedProperty<IJp2pProperties, Object> mp = this.prefs.getSource().getManagedProperty(id);
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
	public boolean setProperty( IJp2pProperties id, Object value ){
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
