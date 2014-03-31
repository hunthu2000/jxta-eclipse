package net.jp2p.endpoint.servlethttp.factory;

import java.util.Collection;

import net.jp2p.chaupal.jxta.module.AbstractModuleFactory;
import net.jp2p.endpoint.servlethttp.Activator;
import net.jxse.module.IJxtaModuleService;
import net.jxta.impl.endpoint.servlethttp.ServletHttpTransport;

public class HttpModuleFactory extends AbstractModuleFactory<ServletHttpTransport> {

	public HttpModuleFactory( HttpPropertySource source ) {
		super(source, Activator.getModuleFactoryRegistrator() );
    }

	@Override
	protected void addModules(
			Collection<IJxtaModuleService<ServletHttpTransport>> modules) {
		modules.add( new HttpModule( (HttpPropertySource) super.getSource() ));
	}
}
