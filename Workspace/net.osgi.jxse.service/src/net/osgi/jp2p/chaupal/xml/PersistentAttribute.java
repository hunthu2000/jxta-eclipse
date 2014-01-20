package net.osgi.jp2p.chaupal.xml;

import java.util.HashMap;
import java.util.Map;

import net.osgi.jp2p.chaupal.xml.PreferenceStore.Persistence;
import net.osgi.jp2p.chaupal.xml.PreferenceStore.SupportedAttributes;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class PersistentAttribute{
	
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

	public String getKey() {
		return key;
	}
	void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}
	
	void setValue(String value) {
		this.value = value;
	}
	
	public Persistence getPersistence() {
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
