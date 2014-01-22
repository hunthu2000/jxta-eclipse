package net.osgi.jp2p.chaupal.persistence;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import net.jp2p.container.persistence.AbstractPersistedProperty;
import net.jp2p.container.properties.AbstractJp2pPropertySource;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IPropertyConvertor;
import net.jp2p.container.properties.ManagedProperty;

public class PersistedProperties extends AbstractPersistedProperty<String>{

	private IScopeContext scope;
	private IPropertyConvertor<String, Object> convertor;
	
	public PersistedProperties( IScopeContext scope, IPropertyConvertor<String, Object> convertor ) {
		super();
		this.scope = scope;
		this.convertor = convertor;
	}

	/**
	 * Get the persisted property with the given id, if it exists
	 * @param id
	 * @return
	 */
	public String getProperty( IJp2pProperties id ){
		ManagedProperty<IJp2pProperties, Object> mp = super.getSource().getManagedProperty(id);
		IPreferencesService service = Platform.getPreferencesService();
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) super.getSource();
		Preferences pref1 = scope.getNode( AbstractJp2pPropertySource.getBundleId(source));
		Preferences[] nodes = new Preferences[] {pref1};
		String defaultValue = convertor.convertFrom( mp.getKey() );
		String value = service.get( id.toString(), defaultValue, nodes );
		return value;
	}
	
	/**
	 * Set the property of a preference store
	 * @param id
	 * @param value
	 * @return
	 */
	public boolean setProperty( IJp2pProperties id, String value ){
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) super.getSource();
		Preferences pref1 = scope.getNode( AbstractJp2pPropertySource.getBundleId(source));
		pref1.put( id.toString(), value.toString());
		try {
			pref1.flush();
			return true;
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return false;
	}	
}
