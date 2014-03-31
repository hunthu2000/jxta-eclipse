package net.jp2p.endpoint.netty.factory.tcp;

import java.util.Collection;

import net.jp2p.chaupal.jxta.module.AbstractModuleFactory;
import net.jp2p.endpoint.netty.Activator;
import net.jp2p.jxta.transport.TransportPropertySource;
import net.jxse.module.IJxtaModuleService;
import net.jxta.impl.endpoint.netty.NettyTransport;

public class TcpModuleFactory extends AbstractModuleFactory<NettyTransport> {

	public TcpModuleFactory( TransportPropertySource source ) {
		super(source, Activator.getModuleFactoryRegistrator() );
    }

	@Override
	protected void addModules(
			Collection<IJxtaModuleService<NettyTransport>> modules) {
		modules.add( new TcpModule( (TransportPropertySource) super.getSource() ));
	}
}
