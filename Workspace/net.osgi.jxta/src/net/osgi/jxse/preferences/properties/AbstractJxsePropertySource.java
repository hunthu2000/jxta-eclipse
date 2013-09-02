package net.osgi.jxse.preferences.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJxsePropertySource< T extends Enum<T>, U extends Enum<U>> implements IJxsePropertySource<T, U> {

	private Map<T,Object> properties;
	private Map<U,Object> directives;
	private Collection<IJxsePropertySource<?,?>> children;

	private int depth = 0;

	public AbstractJxsePropertySource() {
		this(0);
	}

	protected AbstractJxsePropertySource( int depth ) {
		properties = new HashMap<T,Object>();
		directives = new HashMap<U,Object>();
		children = new ArrayList<IJxsePropertySource<?,?>>();
		this.depth = depth;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public Object getProperty(T id) {
		return properties.get( id );
	}

	@Override
	public boolean setProperty(T id, Object value) {
		if( value == null )
			return false;
		properties.put( id, value );
		return true;
	}

	@Override
	public Object getDirective(U id) {
		return directives.get( id );
	}

	@Override
	public boolean setDirective(U id, Object value) {
		if( value == null )
			return false;
		directives.put( id, value );
		return true;
	}

	protected void addChild( IJxsePropertySource<?, ?> child ){
		this.children.add( child );
	}

	protected void removeChild( IJxsePropertySource<?, ?> child ){
		this.children.remove( child );
	}

	@Override
	public IJxsePropertySource<?, ?>[] getChildren() {
		return this.children.toArray(new IJxsePropertySource[children.size()]);
	}
}