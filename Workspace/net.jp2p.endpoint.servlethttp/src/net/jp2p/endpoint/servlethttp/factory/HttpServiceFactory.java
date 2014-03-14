package net.jp2p.endpoint.servlethttp.factory;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.endpoint.servlethttp.Activator;
import net.jp2p.endpoint.servlethttp.osgi.ModuleFactory;
import net.jp2p.jxta.network.configurator.partial.PartialNetworkConfigFactory;

public class HttpServiceFactory extends PartialNetworkConfigFactory<Object> {

	public static final String S_HTTP_SERVICE = "HttpService";

	public HttpServiceFactory(IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parentSource) {
		super(container, S_HTTP_SERVICE, parentSource);
		Activator.getModuleFactoryRegistrator().register( new ModuleFactory());
	}	
}
