package net.osgi.jp2p.utils;

import net.osgi.jp2p.properties.IJp2pProperties;

public class StringProperty implements IJp2pProperties {

	private String key;
	
	public StringProperty( String key ) {
		this.key = key;
	}

	@Override
	public String name() {
		return key;
	}

	@Override
	public String toString() {
		return StringStyler.prettyString(key);
	}
}
