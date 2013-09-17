package net.osgi.jxse.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.osgi.jxse.utils.Utils;

public class ManagedProperty<T extends Enum<T>, U extends Object> {

	
	private T id;
	private U value, defaultValue;
	private Map<String, String> attributes;
	private boolean dirty;

	private Collection<IManagedPropertyListener<T,U>> listeners;
	
	public ManagedProperty( T id ) {
		super();
		this.id = id;
		attributes = new HashMap<String, String>();
		this.listeners = new ArrayList<IManagedPropertyListener<T,U>>();
	}

	public ManagedProperty( T id, U value ) {
		this( id );
		this.value = value;
		this.defaultValue = value;
	}

	public void addManagedPropertyListener( IManagedPropertyListener<T,U> listener ){
		this.listeners.add(listener);
	}

	public void removeManagedPropertyListener( IManagedPropertyListener<T,U> listener ){
		this.listeners.remove(listener);
	}
	
	protected void notifyValueChanged(){
		for( IManagedPropertyListener<T,U> listener: this.listeners ){
			listener.notifyValueChanged( new ManagedPropertyEvent<>(this));
		}
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
		if( value == null )
			return this.defaultValue;
		return value;
	}

	public void setValue(U value) {
		this.dirty = (this.value == null ) || ( !this.value.equals( value ));
		if( !this.dirty )
			return;
		this.value = value;
		this.notifyValueChanged();
	}

	public U getDefaultValue() {
		return defaultValue;
	}

	public boolean isDefault() {
		if((  this.value == null) && ( this.defaultValue == null ))
			return true;
		return ( this.value.equals( this.defaultValue ));
	}	

	public boolean isDirty() {
		return dirty;
	}	
}