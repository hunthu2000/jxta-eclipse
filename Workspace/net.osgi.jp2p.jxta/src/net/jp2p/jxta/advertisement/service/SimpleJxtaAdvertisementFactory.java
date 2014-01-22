package net.jp2p.jxta.advertisement.service;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.jxta.document.Advertisement;

public class SimpleJxtaAdvertisementFactory extends
		AbstractJxtaAdvertisementFactory<Advertisement, Advertisement> {

	public SimpleJxtaAdvertisementFactory(IContainerBuilder container,
			AdvertisementTypes type, IJp2pPropertySource<IJp2pProperties> parentSource) {
		super(container, type, parentSource);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Advertisement createAdvertisement(IJp2pPropertySource<IJp2pProperties> source) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected IJp2pComponent<Advertisement> createComponent(Advertisement advertisement) {
		return (IJp2pComponent<Advertisement>) this.createAdvertisement( super.getPropertySource());
	}

}
