package net.osgi.jxse.utils;

import net.osgi.jxse.properties.IJxseDirectives;

public class StringDirective implements IJxseDirectives{

	private String key;
	
	public StringDirective( String key ) {
		this.key = key;
	}

	@Override
	public String name() {
		return key;
	}

	@Override
	public String toString() {
		return this.key;
	}	
}
