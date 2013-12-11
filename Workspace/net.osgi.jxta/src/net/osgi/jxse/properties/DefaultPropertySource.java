package net.osgi.jxse.properties;

public class DefaultPropertySource<T extends Object> extends AbstractJxseWritePropertySource<T> {

	public DefaultPropertySource(String bundleId, String identifier,
			String componentName) {
		super(bundleId, identifier, componentName);
	}

	public DefaultPropertySource(String bundleId, String identifier,
			String componentName, int depth) {
		super(bundleId, identifier, componentName, depth);
	}

	public DefaultPropertySource(String componentName,
			IJxsePropertySource<?,IJxseDirectives> parent) {
		super(componentName, parent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getIdFromString(String key) {
		return (T) key;
	}

	@Override
	public boolean validate(Object id, Object value) {
		return true;
	}

}
