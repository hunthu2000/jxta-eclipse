package net.osgi.jxse.properties;

import java.net.URISyntaxException;

public abstract class AbstractPreferences<T extends Object, U extends IJxseDirectives> {

	private IJxseWritePropertySource<T, U> source;
	
	public AbstractPreferences( IJxseWritePropertySource<T, U> source ) {
		this.source = source;
	}

	protected IJxseWritePropertySource<T, U> getSource() {
		return source;
	}

	/**
	 * Set the correct property from the given string
	 * @param property
	 * @param value
	 * @return
	 * @throws URISyntaxException
	 */
	public abstract boolean setPropertyFromString( T id, String value );
}
