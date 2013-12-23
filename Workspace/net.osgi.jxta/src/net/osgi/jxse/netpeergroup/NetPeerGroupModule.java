package net.osgi.jxse.netpeergroup;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory.Components;

public class NetPeerGroupModule extends AbstractJxseModule<NetPeerGroupService, NetPeerGroupPropertySource> {

	private NetworkManager manager;
	
	public NetPeerGroupModule(IJxseModule<?> parent) {
		super(parent, 4);
		this.manager = null;
	}

	@Override
	public String getComponentName() {
		return Components.NET_PEERGROUP_SERVICE.toString();
	}

	@Override
	public NetPeerGroupPropertySource onCreatePropertySource() {
		NetPeerGroupPropertySource source = new NetPeerGroupPropertySource( super.getParent().getPropertySource());
		return source;
	}

	
	@Override
	public NetPeerGroupFactory onCreateFactory() {
		return new NetPeerGroupFactory( super.getPropertySource(), this.manager );
	}

	@Override
	public void notifyCreated(ComponentBuilderEvent<Object> event) {
		if( Components.NETWORK_MANAGER.toString().equals( event.getModule().getComponentName() )){
			if( NetPeerGroupPropertySource.isAutoStart(super.getPropertySource() )){
				this.manager = (NetworkManager) event.getModule().createFactory().createComponent();
				
			}
		}
	}
}
