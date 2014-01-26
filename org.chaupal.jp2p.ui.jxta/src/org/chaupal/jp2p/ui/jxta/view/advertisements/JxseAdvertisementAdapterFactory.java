package org.chaupal.jp2p.ui.jxta.view.advertisements;

import net.jxta.document.Advertisement;

import org.chaupal.jp2p.ui.jxta.property.advertisement.AdvertisementPropertySource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

public class JxseAdvertisementAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		  if (adapterType== IPropertySource.class && adaptableObject instanceof Advertisement){
		      return new AdvertisementPropertySource((Advertisement) adaptableObject);
		    }
		    return null;	
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class<?>[]{IPropertySource.class };
	}
}
