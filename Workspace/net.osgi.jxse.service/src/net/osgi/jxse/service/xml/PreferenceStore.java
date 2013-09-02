package net.osgi.jxse.service.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.osgi.jxse.service.xml.PreferenceStore.Persistence;
import net.osgi.jxse.service.xml.PreferenceStore.SupportedAttributes;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.service.prefs.Preferences;

public class PreferenceStore
{
	/**
	 * Persitence options
	 * @author Kees
	 *
	 */
	public enum Persistence{
		NULL,
		TRANSIENT,
		PARSED,
		GENERATED,
		PREFERENCES; //Preferences take over if they are found, otherwise a default values is parsed
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	/**
	 * Supported attributes during parsing
	 * @author Kees
	 *
	 */
	public enum SupportedAttributes{
		ID,
		NAME,
		PERSIST;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public static final String JXTA_SETTINGS = "jxta.settings";

	private Preferences jxtaPreferences;
	private Collection<PersistentAttribute> attributes;
	
	public PreferenceStore( String plugin_id )
	{
		attributes = new ArrayList<PersistentAttribute>();
		Preferences preferences = ConfigurationScope.INSTANCE.getNode( plugin_id );
		jxtaPreferences = preferences.node(JXTA_SETTINGS);
	}

	String getValue( String key ){
		PersistentAttribute pa = this.getAttribute(key);
		if( pa == null )
			return null;
		
		if( !pa.isPersistent() )
			return pa.getValue();
		return this.jxtaPreferences.get( key , pa.getValue());
	}

	public void setValue( String key, String value ){
		PersistentAttribute pa = getAttribute( key );
		if( pa == null )
			this.addPersistentAttribute(key, value);
		switch( pa.getPersistence() ){
		case GENERATED:
		case PARSED:
		case PREFERENCES:
			this.jxtaPreferences.put(key, value);
			break;
		default:
			return;
		}	
		
	}
	
	void addPersistentAttribute( Map<SupportedAttributes, String> attributes, String key, String value){
		this.attributes.add( new PersistentAttribute( attributes, key, value ));
	}

	void addPersistentAttribute( String key, String value){
		this.attributes.add( new PersistentAttribute(key, value ));
	}

	/**
	 * Get the persistent attribute for the given key
	 * @param key
	 * @return
	 */
	PersistentAttribute getAttribute( String key ){
		if(( key == null ) || ( key.length() == 0 ))
			return null;
		for( PersistentAttribute pa: this.attributes ){
			if( key.equals( pa.getKey()))
					return pa;
		}
		return null;
	}

	/**
	 * Get the persistent attribute for the given key
	 * @param key
	 * @return
	 */
	public Persistence getPersistent( String key ){
		if(( key == null ) || ( key.length() == 0 ))
			return Persistence.NULL;
		for( PersistentAttribute pa: this.attributes ){
			if( key.equals( pa.getKey()))
					return pa.getPersistence();
		}
		return Persistence.PARSED;
	}
	
	public boolean isEmpty(){
		return this.attributes.isEmpty();
	}

	public int size(){
		return this.attributes.size();
	}	
}

class PersistentAttribute{
	
	private Map<SupportedAttributes, String> attributes;
	private String key, value;
	
	
	public PersistentAttribute() {
		this( new HashMap<SupportedAttributes, String>());
		this.attributes.put(SupportedAttributes.PERSIST, Persistence.NULL.toString());
	}

	public PersistentAttribute( Map<SupportedAttributes, String> attributes ) {
		super();
		this.attributes = attributes;
	}

	public PersistentAttribute( String key, String defaultValue ) {
		this();
		this.key = key;
		this.value = defaultValue;
	}

	public PersistentAttribute( Map<SupportedAttributes, String> attributes, String key, String defaultValue ) {
		this( attributes );
		this.key = key;
		this.value = defaultValue;
	}

	String getKey() {
		return key;
	}
	void setKey(String key) {
		this.key = key;
	}

	String getValue() {
		return value;
	}
	
	void setValue(String value) {
		this.value = value;
	}
	
	Persistence getPersistence() {
		String str = this.attributes.get( SupportedAttributes.PERSIST );
		if( Utils.isNull(str))
			return Persistence.NULL;
		return Persistence.valueOf( StringStyler.styleToEnum( str));
	}
	
	boolean isPersistent() {
		Persistence persistence = getPersistence();
		return persistence.ordinal() > 1;
	}
}
