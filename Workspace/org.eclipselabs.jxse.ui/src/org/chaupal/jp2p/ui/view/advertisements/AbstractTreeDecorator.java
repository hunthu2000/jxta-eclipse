package org.chaupal.jp2p.ui.view.advertisements;

public abstract class AbstractTreeDecorator<T,U,V extends Object>{

	private T object;
	
	private U parent;
	
	public AbstractTreeDecorator( T object ) {
		this( object, null );
	}

	public AbstractTreeDecorator( T object, U parent ) {
		this.object = object;
		this.parent = parent;
	}

	public U getParent(){
		if( this.parent == null )
			return null;
		return parent;
	}
	
	public T getObject(){
		return object;
	}
	
	public String getLabel(){
		return this.object.toString();
	}
	
	public boolean isRoot(){
		return (this.parent == null );
	}
	
	public boolean hasChildren(){
		return (( getChildren() != null ) && ( getChildren().length > 0 ));
	}
	
	public abstract V[] getChildren();
}
