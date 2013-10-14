package net.osgi.jxse.properties;

public abstract class AbstractJxseValidator<T, U> implements IJxseValidator<T, U> {

	private static final String S_ERR_NULL_VALUE = "The value may not be null for ";

	private T id;
	private boolean nullable;
	
	public AbstractJxseValidator(T id, boolean nullable) {
		this.id = id;
		this.nullable = nullable;
	}

	public AbstractJxseValidator(T id) {
		this( id, false );
	}

	/**
	 * returns true if the value can be null
	 * @return
	 */
	public boolean isNullable() {
		return nullable;
	}

	protected T getId() {
		return id;
	}
	
	@Override
	public boolean validate(Object value) {
		boolean retval = true;
		if( value == null )
			retval = isNullable();
		return validate( value, retval, S_ERR_NULL_VALUE + this.id);
	}

	/**
	 * Helper function returns the given message if check is false
	 * @param value
	 * @param check
	 * @param msg
	 * @return
	 */
	protected boolean validate(Object value, boolean check, String msg ) {
		if( !check )
			throw new ValidationException( msg );
		return check;	
	}
}
