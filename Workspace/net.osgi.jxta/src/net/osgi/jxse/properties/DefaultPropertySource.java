package net.osgi.jxse.properties;

public class DefaultPropertySource extends AbstractJxseWritePropertySource {

	public DefaultPropertySource(String bundleId, String identifier,
			String componentName) {
		super(bundleId, identifier, componentName);
	}

	public DefaultPropertySource(String bundleId, String identifier,
			String componentName, int depth) {
		super(bundleId, identifier, componentName, depth);
	}

	public DefaultPropertySource(String componentName,
			IJxsePropertySource<IJxseProperties> parent) {
		super(componentName, parent);
	}
}
