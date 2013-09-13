package net.osgi.jxse.properties;

import java.util.HashMap;
import java.util.Map;

import net.osgi.jxse.utils.Utils;

public class ManagedProperty<T,U extends Object> {

	
	private T id;
	private U value, defaultValue;
	private Map<String, String> attributes;
	private boolean dirty;
	
	public ManagedProperty( T id ) {
		super();
		this.id = id;
		attributes = new HashMap<String, String>();
	}

	public ManagedProperty( T id, U value ) {
		this( id );
		this.value = value;
		this.defaultValue = value;
	}

	public boolean addAttribute( String attr, String value ){
		if( Utils.isNull( attr ) || ( Utils.isNull( value )))
			return false;
		attributes.put(attr, value);
		return true;
	}

	public boolean removeAttribute( String attr ){
		if( Utils.isNull( attr ))
			return false;
		String str = attributes.remove( attr );
		return !Utils.isNull(str);
	}
	
	public String getAttribute( String attr ){
		return this.attributes.get(attr);
	}

	public T getKey() {
		return id;
	}

	public U getValue() {
		return value;
	}

	public void setValue(U value) {
		this.dirty = (this.value == null ) || ( !this.value.equals( value ));
		this.value = value;
	}

	public U getDefaultValue() {
		return defaultValue;
	}

	public boolean isDirty() {
		return dirty;
	}	
}