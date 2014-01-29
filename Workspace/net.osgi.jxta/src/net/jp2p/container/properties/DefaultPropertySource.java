package net.jp2p.container.properties;

public class DefaultPropertySource extends AbstractJp2pWritePropertySource {

	public DefaultPropertySource(String bundleId, String componentName) {
		super(bundleId, componentName);
	}

	public DefaultPropertySource(String componentName,
			IJp2pPropertySource<IJp2pProperties> parent) {
		super(componentName, parent);
	}
}