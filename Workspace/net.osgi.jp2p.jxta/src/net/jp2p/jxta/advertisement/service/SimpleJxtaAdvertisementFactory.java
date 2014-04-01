package net.jp2p.jxta.advertisement.service;

import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jxta.document.Advertisement;

public class SimpleJxtaAdvertisementFactory extends
		AbstractJxtaAdvertisementFactory<Advertisement, Advertisement> {

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
