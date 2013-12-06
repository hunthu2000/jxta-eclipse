package net.osgi.jxse.builder;

import java.util.ArrayList;
import java.util.Collection;

public class BuilderContainer {

	private Collection<IJxseModule<?,?,?>> modules;
	
	public BuilderContainer() {
		modules = new ArrayList<IJxseModule<?,?,?>>();
	}

	public boolean addModule( IJxseModule<?,?,?> module ){
		return this.modules.add( module );
	}

	public boolean removeModule( IJxseModule<?,?,?> module ){
		return this.modules.remove( module );
	}
	
	public IJxseModule<?,?,?> getModule( String name ){
		for( IJxseModule<?,?,?> module: modules ){
			if( module.getName().equals(name ))
					return module;
		}
		return null;	
	}
}
