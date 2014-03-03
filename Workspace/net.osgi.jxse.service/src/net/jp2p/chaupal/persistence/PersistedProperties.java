package net.jp2p.chaupal.persistence;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import net.jp2p.container.context.IJp2pContext;
import net.jp2p.container.persistence.AbstractPersistedProperty;
import net.jp2p.container.properties.AbstractJp2pPropertySource;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IPropertyConvertor;
import net.jp2p.container.properties.ManagedProperty;

public class PersistedProperties extends AbstractPersistedProperty<String>{

	private IScopeContext scope;
	private IJp2pContext<?> context;
	
	public PersistedProperties( IJp2pWritePropertySource<IJp2pProperties> source, IScopeContext scope ) {
		super( source );
		this.scope = scope;
	}

	public void setContext(IJp2pContext<?> context) {
		this.context = context;
	}

	/**
	 * Clear the property
	 * @param source
	 */
	public void clear( IJp2pPropertySource<IJp2pProperties> source ){
		Preferences pref1 = scope.getNode( AbstractJp2pPropertySource.getBundleId(source) + "." + AbstractJp2pPropertySource.getIdentifier(source));
		try {
			pref1.clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the persisted property with the given id, if it exists
	 * @param id
	 * @return
	 */
	public String getProperty( IJp2pPropertySource<IJp2pProperties> source, IJp2pProperties id ){
		IPreferencesService service = Platform.getPreferencesService();
		Preferences pref1 = scope.getNode( AbstractJp2pPropertySource.getBundleId(source) + "." + AbstractJp2pPropertySource.getIdentifier(source));
		Preferences[] nodes = new Preferences[] {pref1};
		IPropertyConvertor<String, Object> convertor = context.getConvertor((IJp2pWritePropertySource<IJp2pProperties>) source);
		String defaultValue = convertor.convertFrom( id );
		String value = service.get( id.toString(), defaultValue, nodes );
		return value;
	}
	
	/**
	 * Set the property of a preference store
	 * @param id
	 * @param value
	 * @return
	 */
	public boolean setProperty( IJp2pPropertySource<IJp2pProperties> source, IJp2pProperties id, String value ){
		ManagedProperty<IJp2pProperties, Object> mp = source.getManagedProperty(id);
		if( !ManagedProperty.isPersisted(mp))
			return false;
		Preferences pref1 = scope.getNode( AbstractJp2pPropertySource.getBundleId(source) + "." + AbstractJp2pPropertySource.getIdentifier(source));
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
