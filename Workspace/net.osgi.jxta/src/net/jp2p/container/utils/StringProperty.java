package net.jp2p.container.utils;

import net.jp2p.container.properties.IJp2pProperties;

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
