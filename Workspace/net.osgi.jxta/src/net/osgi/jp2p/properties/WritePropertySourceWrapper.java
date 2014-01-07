package net.osgi.jp2p.properties;

public class WritePropertySourceWrapper<T extends Object> extends PropertySourceWrapper<T> implements
		IJp2pWritePropertySource<T> {

	public WritePropertySourceWrapper(IJp2pPropertySource<T> source,
			boolean parentIsSource) {
		super(source, parentIsSource);
	}

	public WritePropertySourceWrapper(IJp2pPropertySource<T> source) {
		super(source);
	}

	@Override
	public ManagedProperty<T, Object> getOrCreateManagedProperty(T id,
			Object value, boolean derived) {
		IJp2pPropertySource<T> source = super.getSource();
		if(!(source instanceof IJp2pPropertySource))
			return super.getManagedProperty(id);
		IJp2pWritePropertySource<T> ws = (IJp2pWritePropertySource<T>) source;
		return ws.getOrCreateManagedProperty(id, value, derived);
	}

	@Override
	public boolean setProperty(T id, Object value) {
		IJp2pPropertySource<T> source = super.getSource();
		if(!(source instanceof IJp2pPropertySource))
			return false;
		IJp2pWritePropertySource<T> ws = (IJp2pWritePropertySource<T>) source;
		return ws.setProperty(id, value );
	}

	@Override
	public boolean setDirective(IJp2pDirectives id, String value) {
		IJp2pPropertySource<T> source = super.getSource();
		if(!(source instanceof IJp2pPropertySource))
			return false;
		IJp2pWritePropertySource<T> ws = (IJp2pWritePropertySource<T>) source;
		return ws.setDirective(id, value);
	}

}
