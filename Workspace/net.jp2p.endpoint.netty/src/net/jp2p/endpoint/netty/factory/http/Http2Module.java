package net.jp2p.endpoint.netty.factory.http;

import net.jp2p.jxta.transport.TransportPropertySource;
import net.jxta.peergroup.IModuleDefinitions;
import net.jxta.platform.ModuleClassID;

class Http2Module extends HttpModule {

    Http2Module( TransportPropertySource source ) {
		super(source);
	}
   
	@Override
	public ModuleClassID getModuleClassID() {
		return IModuleDefinitions.http2ProtoClassID;
	}
}
