package net.jp2p.container.utils;

import net.jp2p.container.properties.IJp2pDirectives;

public class StringDirective implements IJp2pDirectives{

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
		return StringStyler.prettyString(key);
	}
}
