package net.osgi.jp2p.properties;

public interface IJp2pValidator<T,U> {

	static final String S_MSG_VALIDATION_OK = "No problems were found while validating";
	
	/**
	 * Validate the given value
	 * @return
	 */
	public boolean validate( U value);
	
	/**
	 * Returns a message concerning the validation result
	 * @return
	 */
	public String getMessage();
}
