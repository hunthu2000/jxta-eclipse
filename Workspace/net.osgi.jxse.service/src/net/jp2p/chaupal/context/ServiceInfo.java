package net.jp2p.chaupal.context;

import net.jp2p.container.utils.Utils;

public class ServiceInfo {

	String name;
	String context;
	boolean found;
	
	public ServiceInfo( String name, String context) {
		super();
		this.name = name;
		this.context = context;
		this.found = !Utils.isNull( name );
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}
}
