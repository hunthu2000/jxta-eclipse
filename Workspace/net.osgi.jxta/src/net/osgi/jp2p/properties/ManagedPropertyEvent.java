package net.osgi.jp2p.properties;

import java.util.EventObject;

public class ManagedPropertyEvent<T,U extends Object> extends EventObject {
	private static final long serialVersionUID = 1L;

	public ManagedPropertyEvent( ManagedProperty<T,U> property) {
		super(property);
	}
	
	@SuppressWarnings("unchecked")
	public T getId(){
		ManagedProperty<T,U> property = (ManagedProperty<T, U>) super.getSource();
		return property.getKey();
	}

	@SuppressWarnings("unchecked")
	public U getValue(){
		ManagedProperty<T,U> property = (ManagedProperty<T, U>) super.getSource();
		return property.getValue();
	}
	
	@SuppressWarnings("unchecked")
	public ManagedProperty<T,U> getProperty(){
		return (ManagedProperty<T, U>) this.getSource();
	}

}
