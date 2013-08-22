package org.eclipselabs.jxse.ui.view.advertisements;

import net.jxta.document.Advertisement;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipselabs.jxse.ui.property.advertisement.AdvertisementPropertySource;

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
