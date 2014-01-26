package net.jp2p.container.utils;

import java.util.ArrayList;
import java.util.Collection;

public class SimpleNode<T, U extends Object> implements IDescendant<T, ILeaf<U>> {

	private T data;
	private INode<U,?> parent;
	private Collection<ILeaf<U>> children;
	
	public SimpleNode( T data) {
		this.data = data;
		this.children = new ArrayList<ILeaf<U>>();
	}

	public SimpleNode( T data, INode<U,?> parent ) {
		this( data );
		this.parent = parent;
	}

	public T getData() {
		return data;
	}

	void setParent(INode<U, ?> parent) {
		this.parent = parent;
	}

	@Override
	public boolean isRoot() {
		return ( this.parent == null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean addChild( ILeaf<U> child) {
		if( child instanceof SimpleNode )
			((SimpleNode) child).setParent(this);
		return this.children.add( child );
	}

	@Override
	public void removeChild(ILeaf<U> child) {
		this.children.remove( child );
	}

	@SuppressWarnings("unchecked")
	@Override
	public ILeaf<U>[] getChildren() {
		return this.children.toArray( new ILeaf[ this.children.size() ]);
	}

	@Override
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ILeaf<U> getParent() {
		return (ILeaf<U>) parent;
	}

}
