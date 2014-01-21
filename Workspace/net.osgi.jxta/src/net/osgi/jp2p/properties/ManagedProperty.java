package net.osgi.jp2p.properties;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.osgi.jp2p.properties.IManagedPropertyListener.PropertyEvents;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class ManagedProperty<T, U extends Object> {
	
	public static final String S_DEFAULT_CATEGORY = "JP2P";
	
	public enum Attributes{
		PERSIST,
		CREATE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	
	private T id;
	private U value, defaultValue;
	private Map<String, String> attributes;
	private boolean dirty;
	private boolean readOnly;
	private IJp2pValidator<T,U> validator;
	private boolean derived;
	private String category;
	private Collection<IManagedPropertyListener<T,U>> listeners;

	public ManagedProperty( T id ) {
		this( id, null, S_DEFAULT_CATEGORY );
	}
	
	public ManagedProperty( T id, U value, boolean derived, boolean readOnly, String category ) {
		super();
		this.id = id;
		this.readOnly = readOnly;
		this.category = category;
		this.derived = derived;
		this.listeners = new HashSet<IManagedPropertyListener<T,U>>();
		attributes = new HashMap<String, String>();
		this.defaultValue = value;
		this.setValue(value, PropertyEvents.DEFAULT_VALUE_SET);
		this.dirty = false;
	}

	public ManagedProperty( T id, U value ) {
		this( id, value, false, true,  S_DEFAULT_CATEGORY );
	}

	public ManagedProperty( T id, U value, String category ) {
		this( id, value, false, true, category );
	}

	/**
	 * Create a managed property with the given id and value. If derives is true, it means that the property
	 * is managed by another one.
	 * @param id
	 * @param value
	 * @param derived
	 */
	public ManagedProperty( T id, U value, boolean derived ) {
		this( id, value, derived, true, S_DEFAULT_CATEGORY );
	}

	/**
	 * Create a managed property with the given id and value. If derives is true, it means that the property
	 * is managed by another one.
	 * @param id
	 * @param value
	 * @param derived
	 */
	public ManagedProperty( T id, U value, String category, boolean derived ) {
		this( id, value, derived );
		this.category = category;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * Returns true if the property is read only
	 * @return
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	protected void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * A derived property is directly associated with another one. If true, a property can
	 * be skipped because it can also be reconstructed from the source. For instance, the home folder
	 * can be included in the context, the network manager and the network configurator. By marking
	 * the property as derived, this property only needs to be set in one compponent. 
	 * @return
	 */
	public boolean isDerived() {
		return derived;
	}

	void addPropertyListener( IManagedPropertyListener<T,U> listener ){
		this.listeners.add(listener);
	}

	void removePropertyListener( IManagedPropertyListener<T,U> listener ){
		this.listeners.remove(listener);
	}
	
	public IJp2pValidator<T, U> getValidator() {
		return validator;
	}

	public void setValidator(IJp2pValidator<T, U> validator) {
		this.validator = validator;
	}

	protected void notifyValueChanged( PropertyEvents event ){
		for( IManagedPropertyListener<T,U> listener: this.listeners ){
			listener.notifyValueChanged( new ManagedPropertyEvent<>(this, event ));
		}
	}

	public boolean addAttribute( String attr, String value ){
		if( Utils.isNull( attr ) || ( Utils.isNull( value )))
			return false;
		attributes.put(attr.toLowerCase(), value);
		return true;
	}

	public boolean removeAttribute( String attr ){
		if( Utils.isNull( attr ))
			return false;
		String str = attributes.remove( attr.toLowerCase() );
		return !Utils.isNull(str);
	}
	
	public String getAttribute( String attr ){
		if( Utils.isNull( attr ))
			return null;
		return this.attributes.get(attr.toLowerCase());
	}

	public Map<String, String> getAttributes(){
		return this.attributes;
	}
	
	public T getKey() {
		return id;
	}

	public U getValue() {
		if( value == null )
			return this.defaultValue;
		return value;
	}

	public boolean setValue(U value) {
		return this.setValue(value, PropertyEvents.VALUE_CHANGED );
	}
	
	protected boolean setValue(U value, PropertyEvents event) {
		this.dirty = (this.value == null ) || ( !this.value.equals( value ));
		if( !this.dirty )
			return false;
		if( !this.validate( value ))
			return false;
		this.value = value;
		this.derived = false;
		this.notifyValueChanged( event );
		return true;
	}

	public U getDefaultValue() {
		return defaultValue;
	}

	void setDefaultValue( U value ){
		this.defaultValue = value;
	}
	
	public boolean isDefault() {
		if((  this.value == null) && ( this.defaultValue == null ))
			return true;
		return ( this.value.equals( this.defaultValue ));
	}	

	public boolean isDirty() {
		return dirty;
	}	
	
	/**
	 * Validate the value against the given validator, or return true if
	 * no validator is used
	 * @return
	 */
	public boolean validate(){
		return this.validate(this.value);
	}

	/**
	 * Validate the value against the given validator, or return true if
	 * no validator is used
	 * @return
	 */
	protected boolean validate( U value ){
		if( this.validator == null )
			return true;
		else
			return this.validator.validate( value);
	}

	
	@Override
	public String toString() {
		return "{"+ this.id + "=" + this.value + "}";
	}

	/**
	 * Returns true if the given property is persisted
	 * @param property
	 * @return
	 */
	public static boolean isPersisted( ManagedProperty<?,?> property){
		if( property == null )
			return false;
		String str = property.getAttribute( ManagedProperty.Attributes.PERSIST.name() );
		if( !Utils.isNull(str))
			return false;
		return Boolean.parseBoolean( property.getAttribute( Attributes.PERSIST.name() ));
	}
	
	/**
	 * Returns true if the given property is created automatically
	 * @param property
	 * @return
	 */
	public static boolean isCreated( ManagedProperty<?,?> property){
		if( property == null )
			return false;
		String str = property.getAttribute( ManagedProperty.Attributes.CREATE.name().toLowerCase() );
		if( !Utils.isNull(str))
			return false;
		return Boolean.parseBoolean( property.getAttribute( Attributes.CREATE.name().toLowerCase() ));

	}
	
}