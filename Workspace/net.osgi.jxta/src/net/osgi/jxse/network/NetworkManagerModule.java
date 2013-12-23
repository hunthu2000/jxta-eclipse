package net.osgi.jxse.network;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.IJxseDirectives.Directives;

public class NetworkManagerModule extends AbstractJxseModule<NetworkManager, NetworkManagerPropertySource> {

	public NetworkManagerModule(IJxseModule<?> parent) {
		super(parent, 2);
	}

	@Override
	public String getComponentName() {
		return Components.NETWORK_MANAGER.toString();
	}

	@Override
	protected NetworkManagerPropertySource onCreatePropertySource() {
		NetworkManagerPropertySource source = new NetworkManagerPropertySource( (JxseContextPropertySource) super.getParent().getPropertySource());
		NetworkManagerPropertySource.setParentDirective(Directives.AUTO_START, source);
		return source;
	}

	@Override
	public IComponentFactory<NetworkManager> onCreateFactory() {
		return new NetworkManagerFactory( super.getPropertySource() );
	}

	@Override
	public void notifyCreated(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
