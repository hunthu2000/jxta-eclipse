package net.osgi.jxse.network;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.network.configurator.NetworkConfigurationPropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxsePropertySource;

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
	public void extendModules() {
		IJxsePropertySource<?> ncps = NetworkManagerPropertySource.findPropertySource(super.getPropertySource(), 
				Components.NETWORK_CONFIGURATOR.toString());
		if( ncps == null )
			super.getPropertySource().addChild( new NetworkConfigurationPropertySource( super.getPropertySource() ));
		super.extendModules();
	}

	@Override
	public IComponentFactory<NetworkManager> onCreateFactory() {
		return new NetworkManagerFactory( super.getPropertySource() );
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
