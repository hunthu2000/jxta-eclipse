package net.osgi.jp2p.advertisement;

import net.jxta.document.Advertisement;

public interface IAdvertisementProvider {
	
	/**
	 * An advertisement provider provides for a list of advertisements
	 * @return
	 */
	public Advertisement[] getAdvertisements();
}
