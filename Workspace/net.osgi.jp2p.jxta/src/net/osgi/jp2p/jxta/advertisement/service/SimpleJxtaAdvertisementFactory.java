package net.osgi.jp2p.jxta.advertisement.service;

import net.jxta.document.Advertisement;
import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

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
