package net.osgi.jxse.component;

import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.IJxseModule;

public class ModuleNode<T extends Object> extends ComponentNode<IJxseModule<T>> {

	public ModuleNode(IJxseModule<T> data) {
		this(data, null);
	}

	public ModuleNode(IJxseModule<T> data, ModuleNode<?> parent) {
		super( data, parent);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ComponentNode<?> addChild(Object data) {
		ModuleNode<?> child = new ModuleNode(( IJxseModule<Object> )data, this );
		super.getChildrenAsCollection().add( child );
		return child;
	}

	
}
