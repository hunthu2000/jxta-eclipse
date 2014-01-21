package net.osgi.jp2p.chaupal.persistence;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import net.osgi.jp2p.properties.AbstractJp2pPropertySource;
import net.osgi.jp2p.properties.AbstractPreferences;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.utils.Utils;

public class PersistedProperty<T extends Object> extends AbstractPreferences<T>{

	private Preferences preferences;
	
	public PersistedProperty(IScopeContext scope ) {
		super();
		IJp2pWritePropertySource<IJp2pProperties> source = super.getSource();
		Preferences pref1 = scope.getNode( AbstractJp2pPropertySource.getBundleId(source));
		Preferences pref2 = pref1.node( source.getId() );
		this.preferences = pref2.node( source.getComponentName() );
	}

	/**
	 * Get the persisted property with the given id, if it exists
	 * @param id
	 * @return
	 */
	public T getProperty( IJp2pProperties id ){
		ManagedProperty<IJp2pProperties, Object> mp = super.getSource().getManagedProperty(id);
		String value = this.preferences.get( id.toString(), mp.getDefaultValue().toString());
		if( !Utils.isNull( value )){
			//this.prefs.setPropertyFromString(id, value);
		}
		return null; // id
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

	@Override
	protected T convertValue(IJp2pProperties id, String value) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
