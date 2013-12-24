package net.osgi.jxse.netpeergroup;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.utils.StringStyler;

public class NetPeerGroupModule extends AbstractJxseModule<NetPeerGroupService, NetPeerGroupPropertySource> {

	private NetworkManager manager;
	private boolean canCreate;
	
	public NetPeerGroupModule(IJxseModule<?> parent) {
		super(parent, 4);
		this.manager = null;
		this.canCreate = false;
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
	public boolean canCreate() {
		return this.canCreate;
	}

	@Override
	public NetPeerGroupFactory onCreateFactory() {
		return new NetPeerGroupFactory( super.getPropertySource(), this.manager );
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		String name = StringStyler.styleToEnum(event.getModule().getComponentName());
		if( !Components.isComponent(name))
			return;

		switch( event.getBuilderEvent()){
		case COMPONENT_CREATED:
			switch(Components.valueOf( name )){

			case NETWORK_MANAGER:
				if( NetPeerGroupPropertySource.isAutoStart(super.getPropertySource() )){
					this.manager = (NetworkManager) event.getModule().getComponent();
				}
				break;
			case NETWORK_CONFIGURATOR:
				this.canCreate = true;
				IComponentFactory<?> factory = this.createFactory();
				factory.complete();
			default:
				break;
			}
			break;
		default:
			break;
		}
	}
}