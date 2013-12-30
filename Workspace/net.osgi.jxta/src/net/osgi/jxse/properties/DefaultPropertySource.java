package net.osgi.jxse.properties;

public class DefaultPropertySource extends AbstractJxseWritePropertySource {

	public DefaultPropertySource(String bundleId, String componentName) {
		super(bundleId, componentName);
	}

	public DefaultPropertySource(String bundleId, String identifier,
			String componentName, int depth) {
		super(bundleId, componentName, depth);
	}

	public DefaultPropertySource(String componentName,
			IJxsePropertySource<IJxseProperties> parent) {
		super(componentName, parent);
	}
}
