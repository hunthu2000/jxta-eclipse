package net.osgi.jxse.builder.container;

import java.util.EventObject;

import net.osgi.jxse.builder.IJxseModule;

public class BuilderContainerEvent<T> extends EventObject {
	private static final long serialVersionUID = -7792060576581837435L;
	
	private IJxseModule<T> module;
	
	public BuilderContainerEvent(Object arg0, IJxseModule<T> module ) {
		super(arg0);
	}

	public IJxseModule<T> getModule() {
		return module;
	}
}
