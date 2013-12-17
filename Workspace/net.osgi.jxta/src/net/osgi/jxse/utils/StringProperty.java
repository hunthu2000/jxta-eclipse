package net.osgi.jxse.utils;

import net.osgi.jxse.properties.IJxseProperties;

public class StringProperty implements IJxseProperties {

	private String key;
	
	public StringProperty( String key ) {
		this.key = key;
	}

	@Override
	public String name() {
		return key;
	}

}
