package net.jp2p.chaupal.jxta.network.configurator.partial;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.factory.AbstractPropertySourceFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;

public class PartialNetworkConfigFactory<T extends Object> extends AbstractPropertySourceFactory {

	private String componentName;
	
	public PartialNetworkConfigFactory( IContainerBuilder container, String componentName, IJp2pPropertySource<IJp2pProperties> parentSource ){
		super( container, parentSource);
		this.componentName = componentName;
	}

	@Override
	public String getComponentName() {
		return componentName;
	}

	@Override
	protected PartialNetworkConfigPropertySource onCreatePropertySource() {
		return new PartialNetworkConfigPropertySource( this.componentName, super.getParentSource() );
	}
}
